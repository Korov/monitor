package org.korov.monitor.websocket;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.repository.KafkaSourceRepository;
import org.korov.monitor.utils.JsonUtils;
import org.korov.monitor.utils.KafkaUtils;
import org.korov.monitor.utils.SpringBootUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketServer implements WebSocketHandler {
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(16, 1024,
            60000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("kafka-consumer-%d").build());

    public void consume(WebSocketSession session, String broker, String topic, String group, String reset, String partition, String offset) {
        THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR.execute(new Thread(() -> {
            KafkaConsumer<String, String> consumer;
            List<TopicPartition> topicPartitions = new ArrayList<>();
            if (partition != null && offset != null && !"null".equals(partition) && !"null".equals(offset)) {
                topicPartitions.add(new TopicPartition(topic, Integer.parseInt(partition)));
                consumer = KafkaUtils.getConsumer(broker, group, reset);
                consumer.assign(topicPartitions);
                long defaultOffset = 0L;
                try {
                    defaultOffset = Long.parseLong(offset);
                } catch (Exception e) {
                    log.error("invalid offset:{}", offset);
                }
                for (TopicPartition topicPartition : topicPartitions) {
                    consumer.seek(topicPartition, defaultOffset);
                }
            } else {
                consumer = KafkaUtils.getConsumer(broker, group, reset);
                topicPartitions = KafkaUtils.getTopicPartitions(broker, topic);
                consumer.assign(topicPartitions);
            }


            while (session.isOpen()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, String> text = new LinkedHashMap<>();
                    text.put("topic", record.topic());
                    text.put("partition", String.valueOf(record.partition()));
                    text.put("offset", String.valueOf(record.offset()));
                    text.put("key", record.key());
                    text.put("value", record.value());
                    try {
                        session.textMessage(JsonUtils.jsonString(text));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.info("kafka consumer closed");
        }));
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Map<String, Object> queryString = session.getAttributes();
        KafkaSourceRepository kafkaSourceRepository = SpringBootUtils.getBean(KafkaSourceRepository.class);
        long sourceId = (long) queryString.get("sourceId");
        Mono<KafkaSource> kafkaSource = kafkaSourceRepository.findById(sourceId);
        String topic = (String) queryString.get("topic");
        String group = (String) queryString.get("group");
        String reset = (String) queryString.get("reset");
        String partition = (String) queryString.get("partition");
        String offset = (String) queryString.get("offset");
        kafkaSource.map(KafkaSource::getBroker).subscribe(broker -> consume(session, broker, topic,group, reset, partition, offset));
        return session.send(session.receive());
    }
}