package org.korov.monitor.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.undertow.websockets.UndertowSession;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.CopyOnWriteMap;
import org.korov.monitor.controller.request.ConsumerRequest;
import org.korov.monitor.controller.request.ConsumerRequestDecoder;
import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.controller.request.KafkaMessageRequestEncoder;
import org.korov.monitor.utils.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(value = "/kafka/consumer/socket", decoders = ConsumerRequestDecoder.class, encoders = KafkaMessageRequestEncoder.class)
@ApplicationScoped
public class WebSockets {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSockets.class);
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(16, 1024,
            60000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("kafka-consumer-%d").build());
    Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    Map<String, Boolean> isRunningMap = new CopyOnWriteMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String remoteAddress = ((UndertowSession) session).getChannel().remoteAddress().toString();
        sessionMap.put(remoteAddress, session);
        isRunningMap.put(remoteAddress, false);
        String queryString = session.getQueryString();
        String[] array = queryString.split("&");
        ConsumerRequest request = new ConsumerRequest();
        for (String s : array) {
            String[] param = s.split("=");
            if (param.length == 2) {
                switch (param[0]) {
                    case "topic":
                        request.setTopic(param[1]);
                        break;
                    case "broker":
                        request.setBroker(param[1]);
                        break;
                    case "group":
                        request.setGroup(param[1]);
                        break;
                    case "reset":
                        request.setReset(param[1]);
                        break;
                    case "partition":
                        request.setPartition(Long.valueOf(param[1]));
                        break;
                    case "offset":
                        request.setOffset(Long.valueOf(param[1]));
                        break;
                    default:
                        break;
                }
            }
        }

        KafkaConsumer<String, String> consumer;
        if (request.getReset() != null && !Objects.equals(request.getReset(), "")) {
            consumer = KafkaUtils.getConsumer(request.getBroker(), request.getGroup());
        } else {
            consumer = KafkaUtils.getConsumer(request.getBroker(), request.getGroup(), request.getReset());
        }
        consumer.subscribe(Collections.singleton(request.getTopic()));
        isRunningMap.put(remoteAddress, true);
        LOGGER.info("host:{} joined, all host:{}, query string:{}", remoteAddress, sessionMap.size(), queryString);
        consume(session, request);
    }

    public void consume(Session session, ConsumerRequest request) {
        THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR.execute(new Thread(() -> {
            KafkaConsumer<String, String> consumer;
            List<TopicPartition> topicPartitions = new ArrayList<>();
            if (request.getPartition() != null && request.getOffset() != null && !"null".equals(request.getReset())) {
                topicPartitions.add(new TopicPartition(request.getTopic(), request.getPartition().intValue()));
                consumer = KafkaUtils.getConsumer(request.getBroker(), request.getGroup(), request.getReset());
                consumer.assign(topicPartitions);
                long defaultOffset = request.getOffset();
                if (defaultOffset > 0) {
                    for (TopicPartition topicPartition : topicPartitions) {
                        consumer.seek(topicPartition, defaultOffset);
                    }
                } else {
                    for (TopicPartition topicPartition : topicPartitions) {
                        consumer.seek(topicPartition, 0);
                    }
                }
            } else {
                consumer = KafkaUtils.getConsumer(request.getBroker(), request.getGroup(), request.getReset());
                topicPartitions = KafkaUtils.getTopicPartitions(request.getBroker(), request.getTopic());
                consumer.assign(topicPartitions);
            }


            while (session.isOpen()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    KafkaMessageRequest message = new KafkaMessageRequest();
                    message.setKey(record.key());
                    message.setMessage(record.value());
                    message.setTopic(record.topic());
                    message.setPartition(record.partition());
                    message.setOffset(record.offset());
                    session.getAsyncRemote().sendObject(message, result -> {
                        if (result.getException() == null) {
                            LOGGER.info("send message success");
                        } else {
                            LOGGER.error("unable sent message, error:{}", result.getException().getMessage());
                            result.getException().printStackTrace();
                        }
                    });
                }
            }
            LOGGER.info("kafka consumer closed");
        }));
    }

    @OnClose
    public void onClose(Session session) {
        String remoteAddress = ((UndertowSession) session).getChannel().remoteAddress().toString();
        isRunningMap.put(remoteAddress, false);
        try (Session session1 = sessionMap.remove(remoteAddress)) {
            String remoteAddress1 = ((UndertowSession) session1).getChannel().remoteAddress().toString();
            if (remoteAddress.equals(remoteAddress1)) {
                LOGGER.info("session:{} closed", remoteAddress);
            } else {
                LOGGER.error("invalid session:{}, session1:{}", remoteAddress, remoteAddress1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String remoteAddress = ((UndertowSession) session).getChannel().remoteAddress().toString();
        isRunningMap.put(remoteAddress, false);
        try (Session session1 = sessionMap.remove(remoteAddress)) {
            String remoteAddress1 = ((UndertowSession) session1).getChannel().remoteAddress().toString();
            if (remoteAddress.equals(remoteAddress1)) {
                LOGGER.info("error session:{} closed, error:{}", remoteAddress, throwable);
            } else {
                LOGGER.error("error session:{}, session1:{}, error:{}", remoteAddress, remoteAddress1, throwable);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(Session session, ConsumerRequest request) {
        String remoteAddress = ((UndertowSession) session).getChannel().remoteAddress().toString();
        isRunningMap.put(remoteAddress, true);
        LOGGER.info("get from:{}, message:{}", remoteAddress, request.toString());
    }
}
