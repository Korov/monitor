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
        List<TopicVO> topicVOList = KafkaUtils.queryTopics("docker.korov.online:9095", null);
        log.info(String.valueOf(topicVOList.size()));
        for (TopicVO topicVO : topicVOList) {
            log.info(topicVO.toString());
        }
    }

    @Test
    void getTopicDetail() {
        TopicDescriptionVO description = KafkaUtils.getTopicDetail("docker.korov.online:9095", "monitor_topic");
        log.info(description.toString());
    }

    @Test
    void createTopic() throws ExecutionException, InterruptedException {
        KafkaUtils.createTopic("docker.korov.online:9095", "monitor_topic112", 10, 1);
        TopicDescriptionVO description = KafkaUtils.getTopicDetail("docker.korov.online:9095", "monitor_topic");
        log.info(description.toString());
    }

    @Test
    void deleteTopic() {
        KafkaUtils.deleteTopic("docker.korov.online:9095", "monitor_topic112");
    }

    @Test
    void getConsumers() {
        List<String> result = KafkaUtils.getConsumers("docker.korov.online:9095", "monitor_topic");
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
        KafkaUtils.produceMessage("docker.korov.online:9095", request);
    }
}