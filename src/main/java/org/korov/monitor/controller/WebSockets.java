package org.korov.monitor.controller;

import io.undertow.websockets.UndertowSession;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
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
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/kafka/consumer/socket", decoders = ConsumerRequestDecoder.class, encoders = KafkaMessageRequestEncoder.class)
@ApplicationScoped
public class WebSockets {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSockets.class);
    Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    Map<String, Boolean> isRunningMap = new CopyOnWriteMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String remoteAddress = ((UndertowSession) session).getChannel().remoteAddress().toString();
        sessionMap.put(remoteAddress, session);
        isRunningMap.put(remoteAddress, false);
        LOGGER.info("host:{} joined, all host:{}", remoteAddress, sessionMap.size());
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

        KafkaConsumer<String, String> consumer;
        if (request.getReset() != null && !Objects.equals(request.getReset(), "")) {
            consumer = KafkaUtils.getConsumer(request.getBroker(), request.getGroup());
        } else {
            consumer = KafkaUtils.getConsumer(request.getBroker(), request.getGroup(), request.getReset());
        }
        consumer.subscribe(Collections.singleton(request.getTopic()));
        while (isRunningMap.get(remoteAddress)) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.of(1, ChronoUnit.SECONDS));
            for (ConsumerRecord<String, String> record : records) {
                KafkaMessageRequest message = new KafkaMessageRequest();
                message.setKey(record.key());
                message.setMessage(record.value());
                message.setTopic(record.topic());
                message.setPartition(record.partition());
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
    }
}
