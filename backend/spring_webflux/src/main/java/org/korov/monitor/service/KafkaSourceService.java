package org.korov.monitor.service;

import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.controller.request.TopicRequest;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.vo.TopicDescriptionVO;
import org.korov.monitor.vo.TopicVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author korov
 */
public interface KafkaSourceService {
    Mono<KafkaSource> addKafkaSource(KafkaSource kafkaSource);

    void deleteKafkaSource(Long id);

    Flux<KafkaSource> queryAllKafkaSource();

    Flux<TopicVO> queryTopics(Long sourceId, String keyword);

    TopicDescriptionVO queryTopicDetail(Long sourceId, String topic);

    void createTopic(TopicRequest request) throws ExecutionException, InterruptedException;

    void deleteTopic(Long sourceId, String topic);

    List<Map<String, Object>> getConsumers(Long sourceId, String topic);

    List<Map<String, Object>> getConsumerDetail(Long sourceId, String group);

    void produceMessage(KafkaMessageRequest request);
}
