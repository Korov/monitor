package org.korov.monitor.utils;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.junit.jupiter.api.Test;
import org.korov.monitor.vo.TopicVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

class KafkaUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUtilsTest.class);

    @Test
    void queryTopics() {
        List<TopicVO> topics = KafkaUtils.queryTopics("localhost:9095", "");
        for (TopicVO topic : topics) {
            LOGGER.info(topic.toString());
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
            LOGGER.info(group.toString());
        }
    }
}