package org.korov.monitor.utils;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.korov.monitor.vo.TopicVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

class KafkaUtilsTest {

    @Test
    void queryTopics() {
        List<TopicVO> topics = KafkaUtils.queryTopics("localhost:9095", "");
        for (TopicVO topic : topics) {
            System.out.println(topic.toString());
        }
    }

    @Test
    void getClusterInfo() {
        KafkaUtils.getClusterInfo("localhost:9095");
    }

    @Test
    void getClient() throws ExecutionException, InterruptedException {
        AdminClient adminClient = KafkaUtils.getClient("localhost:9095");
        Collection<ConsumerGroupListing> groups = adminClient.listConsumerGroups().all().get();
        for (ConsumerGroupListing group : groups) {
            System.out.println(group.toString());
        }
    }

    @RepeatedTest(5)
    void produceMessage() {
        KafkaUtils.produceMessage("localhost:9095", "tp1", "key2", "message2");
    }

    @Test
    void consumerMessage() {
        KafkaConsumer<String, String> consumer = KafkaUtils.getConsumer("localhost:9095", "test1", "earliest");
        consumer.subscribe(Collections.singleton("tp1"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.of(1, ChronoUnit.SECONDS));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("key:%s, message:%s%n", record.key(), record.value());
            }
            consumer.commitAsync();
        }
    }

    @Test
    void getConsumers() throws ExecutionException, InterruptedException {
        List<String>  consumers = KafkaUtils.getConsumers("localhost:9095", null);
        System.out.println(consumers);

        AdminClient adminClient = KafkaUtils.getClient("localhost:9095");
        Collection<ConsumerGroupListing> groupListings = adminClient.listConsumerGroups().all().get();
        System.out.println("debug");
    }
}