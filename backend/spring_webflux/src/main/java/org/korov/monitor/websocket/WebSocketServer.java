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
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketServer implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String queryString = session.getHandshakeInfo().getUri().getQuery();
        String[] array = queryString.split("&");
        ConsumerRequest request = new ConsumerRequest();
        for (String s : array) {
            String[] param = s.split("=");
            if (param.length == 2) {
                switch (param[0]) {
                    case "topic" -> request.setTopic(param[1]);
                    case "broker" -> request.setBroker(param[1]);
                    case "group" -> request.setGroup(param[1]);
                    case "reset" -> request.setReset(param[1]);
                    case "partition" -> request.setPartition(Long.valueOf(param[1]));
                    case "offset" -> request.setOffset(Long.valueOf(param[1]));
                    default -> {
                    }
                }
            }
        }

        log.info("start consumer");
        List<TopicPartition> topicPartitions = new ArrayList<>();
        if (request.getPartition() != null && request.getOffset() != null && !"null".equals(request.getReset())) {
            topicPartitions.add(new TopicPartition(request.getTopic(), request.getPartition().intValue()));
        } else {
            topicPartitions = KafkaUtils.getTopicPartitions(request.getBroker(), request.getTopic());
        }
        KafkaReceiver<String, String> kafkaReceiver = KafkaUtils.getReceiver(request.getBroker(), request.getGroup(),
                request.getReset(), topicPartitions, request.getOffset());

        Flux<WebSocketMessage> socketMessageFlux = kafkaReceiver.receive()
                .doOnNext(record -> {
                            log.info("receive:{}", record.value());
                            record.receiverOffset().acknowledge();
                        }
                ).map(record -> {
                    KafkaMessageRequest message = new KafkaMessageRequest();
                    message.setKey(record.key());
                    message.setMessage(record.value());
                    message.setTopic(record.topic());
                    message.setPartition(record.partition());
                    message.setOffset(record.offset());
                    try {
                        return session.textMessage(JsonUtils.jsonString(message));
                    } catch (JsonProcessingException e) {
                        log.error("invalid value:{}", record.value());
                    }
                    return session.textMessage("");
                });

        return session.send(socketMessageFlux);
    }
}