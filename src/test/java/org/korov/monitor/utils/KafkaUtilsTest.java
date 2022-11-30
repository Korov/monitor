package org.korov.monitor.utils;

import org.junit.jupiter.api.Test;
import org.korov.monitor.vo.TopicVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
}