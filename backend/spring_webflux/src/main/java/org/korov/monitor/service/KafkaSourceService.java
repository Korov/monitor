package org.korov.monitor.service;

import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.controller.request.TopicRequest;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.vo.PageVO;
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

    Mono<Void> deleteKafkaSource(Long id);

    Flux<KafkaSource> queryAllKafkaSource();

    Mono<List<TopicVO>> queryTopics(Long sourceId, String keyword);

    Mono<TopicDescriptionVO> queryTopicDetail(Long sourceId, String topic);

    Mono<KafkaSource> createTopic(TopicRequest request) throws ExecutionException, InterruptedException;

    Mono<Object> deleteTopic(Long sourceId, String topic);

    Mono<List<String>> getConsumers(Long sourceId, String topic);

    Mono<List<Map<String, Object>>> getConsumerDetail(Long sourceId, String group);

    Mono<KafkaSource> produceMessage(KafkaMessageRequest request);

    Mono<PageVO<KafkaSource>> pageQueryKafkaSource(int startPage, int pageSize);
}
