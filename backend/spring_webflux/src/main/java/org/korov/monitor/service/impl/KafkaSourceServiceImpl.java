package org.korov.monitor.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.controller.request.TopicRequest;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.repository.KafkaSourceRepository;
import org.korov.monitor.service.KafkaSourceService;
import org.korov.monitor.utils.KafkaUtils;
import org.korov.monitor.vo.TopicDescriptionVO;
import org.korov.monitor.vo.TopicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author korov
 */
@Slf4j
@Service
public class KafkaSourceServiceImpl implements KafkaSourceService {
    private KafkaSourceRepository kafkaSourceRepository;

    @Autowired
    public void setKafkaSourceRepository(KafkaSourceRepository kafkaSourceRepository) {
        this.kafkaSourceRepository = kafkaSourceRepository;
    }

    @Override
    public Mono<KafkaSource> addKafkaSource(KafkaSource kafkaSource) {
        return kafkaSourceRepository.save(kafkaSource);
    }

    @Override
    public Mono<Void> deleteKafkaSource(Long id) {
       return kafkaSourceRepository.deleteById(id);
    }

    @Override
    public Flux<KafkaSource> queryAllKafkaSource() {
        return kafkaSourceRepository.findAll();
    }

    @Override
    public Mono<List<TopicVO>> queryTopics(Long sourceId, String keyword) {
        Mono<KafkaSource> kafkaSource = kafkaSourceRepository.findById(sourceId);
        return kafkaSource.map(source -> {
            return KafkaUtils.queryTopics(source.getBroker(), keyword);
        });
    }

    @Override
    public Mono<TopicDescriptionVO> queryTopicDetail(Long sourceId, String topic) {
        Mono<KafkaSource> optionalKafkaSource = kafkaSourceRepository.findById(sourceId);
        return optionalKafkaSource.map(kafkaSource -> KafkaUtils.getTopicDetail(kafkaSource.getBroker(), topic));
    }

    @Override
    public Mono<Object> createTopic(TopicRequest request) {
        Mono<KafkaSource> optional = kafkaSourceRepository.findById(request.getSourceId());
        return optional.map(source -> {
            try {
                KafkaUtils.createTopic(source.getBroker(), request.getTopic(), request.getPartition(), request.getReplica());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<Object> deleteTopic(Long sourceId, String topic) {
        Mono<KafkaSource> optional = kafkaSourceRepository.findById(sourceId);
       return  optional.map(value -> {
            KafkaUtils.deleteTopic(value.getBroker(), topic);
            return Mono.empty();
        });
    }

    @Override
    public Mono<List<String>> getConsumers(Long sourceId, String topic) {
        Mono<KafkaSource> optional = kafkaSourceRepository.findById(sourceId);
        return optional.map(source -> KafkaUtils.getConsumers(source.getBroker(), topic));
    }

    @Override
    public Mono<List<Map<String, Object>>> getConsumerDetail(Long sourceId, String group) {
        Mono<KafkaSource> optional = kafkaSourceRepository.findById(sourceId);
        return optional.map(source -> KafkaUtils.getConsumerDetail(source.getBroker(), group));
    }

    @Override
    public void produceMessage(KafkaMessageRequest request) {
        Mono<KafkaSource> optional = kafkaSourceRepository.findById(request.getSourceId());
        optional.map(source -> {
            KafkaUtils.produceMessage(source.getBroker(), request);
            return Mono.empty();
        });
    }
}
