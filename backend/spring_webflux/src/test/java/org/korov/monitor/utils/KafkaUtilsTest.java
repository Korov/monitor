package org.korov.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.vo.TopicDescriptionVO;
import org.korov.monitor.vo.TopicVO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
class KafkaUtilsTest {

    @Test
    void queryTopics() {
        List<TopicVO> topicVOList = KafkaUtils.queryTopics("localhost:9095", null);
        log.info(String.valueOf(topicVOList.size()));
        for (TopicVO topicVO : topicVOList) {
            log.info(topicVO.toString());
        }
    }

    @Test
    void getTopicDetail() {
        TopicDescriptionVO description = KafkaUtils.getTopicDetail("localhost:9095", "monitor_topic");
        log.info(description.toString());
    }

    @Test
    void createTopic() throws ExecutionException, InterruptedException {
        KafkaUtils.createTopic("localhost:9095", "monitor_topic112", 10, 1);
        TopicDescriptionVO description = KafkaUtils.getTopicDetail("localhost:9095", "monitor_topic");
        log.info(description.toString());
    }

    @Test
    void deleteTopic() {
        KafkaUtils.deleteTopic("localhost:9095", "monitor_topic112");
    }

    @Test
    void getConsumers() {
        List<String> result = KafkaUtils.getConsumers("linux.korov.org:9095", "monitor_topic");
        for (String map : result) {
            log.info(map);
        }
    }

    @Test
    void produceMessage() {
        KafkaMessageRequest request = new KafkaMessageRequest();
        request.setMessage("ssss");
        request.setKey("aaa");
        request.setTopic("monitor_topic");
        request.setPartition(2);
        KafkaUtils.produceMessage("linux.korov.org:9095", request);
    }
}