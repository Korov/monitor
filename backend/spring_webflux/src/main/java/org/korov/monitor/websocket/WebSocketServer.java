package org.korov.monitor.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.korov.monitor.controller.request.ConsumerRequest;
import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.utils.JsonUtils;
import org.korov.monitor.utils.KafkaUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class WebSocketServer implements WebSocketHandler {
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(16, 1024,
            60000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("kafka-consumer-%d").build());

    public Mono<Void> consume(WebSocketSession session, String broker, String topic, String group, String reset, Long partition, Long offset) {
        THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);
        AtomicReference<Mono<Void>> input = null;
        THREAD_POOL_EXECUTOR.execute(new Thread(() -> {
            KafkaConsumer<String, String> consumer;
            List<TopicPartition> topicPartitions = new ArrayList<>();
            if (partition != null && offset != null && !"null".equals(reset)) {
                topicPartitions.add(new TopicPartition(topic, partition.intValue()));
                consumer = KafkaUtils.getConsumer(broker, group, reset);
                consumer.assign(topicPartitions);
                long defaultOffset = offset;
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
                consumer = KafkaUtils.getConsumer(broker, group, reset);
                topicPartitions = KafkaUtils.getTopicPartitions(broker, topic);
                consumer.assign(topicPartitions);
            }

            log.info("session open");
            while (session.isOpen()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    KafkaMessageRequest message = new KafkaMessageRequest();
                    message.setKey(record.key());
                    message.setMessage(record.value());
                    message.setTopic(record.topic());
                    message.setPartition(record.partition());
                    try {
                        log.info("send message:{}", JsonUtils.jsonString(message));
                        input.set(session.send(Flux.just(session.textMessage(JsonUtils.jsonString(message)))));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            log.info("kafka consumer closed");
        }));
        return input.get();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String queryString = session.getHandshakeInfo().getUri().getQuery();
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

        log.info("start consumer");
        return consume(session, request.getBroker(), request.getTopic(), request.getGroup(), request.getReset(), request.getPartition(), request.getOffset());
    }
}