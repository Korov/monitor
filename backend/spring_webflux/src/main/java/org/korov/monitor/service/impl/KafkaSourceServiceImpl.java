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
import java.util.Optional;
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
    public void deleteKafkaSource(Long id) {
        kafkaSourceRepository.deleteById(id);
    }

    @Override
    public Flux<KafkaSource> queryAllKafkaSource() {
        return kafkaSourceRepository.findAll();
    }

    @Override
    public Flux<TopicVO> queryTopics(Long sourceId, String keyword) {
        Mono<KafkaSource> kafkaSource = kafkaSourceRepository.findById(sourceId);
        Flux<TopicVO> result = Flux.fromIterable(Collections.emptyList());
        kafkaSource.map(source -> {
            List<TopicVO> topics = KafkaUtils.queryTopics(source.getBroker(), keyword);
            for (TopicVO topic : topics) {
                result.concatWithValues(topic);
            }
            return Mono.empty();
        });
        return result;
    }

    @Override
    public TopicDescriptionVO queryTopicDetail(Long sourceId, String topic) {
        Mono<KafkaSource> optionalKafkaSource = kafkaSourceRepository.findById(sourceId);
        return optionalKafkaSource.map(kafkaSource -> KafkaUtils.getTopicDetail(kafkaSource.getBroker(), topic)).orElse(null);
    }

    @Override
    public void createTopic(TopicRequest request) throws ExecutionException, InterruptedException {
        Optional<KafkaSource> optional = kafkaSourceRepository.findById(request.getSourceId());
        if (optional.isPresent()) {
            KafkaUtils.createTopic(optional.get().getBroker(), request.getTopic(), request.getPartition(), request.getReplica());
        }
    }

    @Override
    public void deleteTopic(Long sourceId, String topic) {
        Optional<KafkaSource> optional = kafkaSourceRepository.findById(sourceId);
        optional.ifPresent(kafkaSource -> KafkaUtils.deleteTopic(kafkaSource.getBroker(), topic));
    }

    @Override
    public List<Map<String, Object>> getConsumers(Long sourceId, String topic) {
        Optional<KafkaSource> optional = kafkaSourceRepository.findById(sourceId);
        if (optional.isPresent()) {
            return KafkaUtils.getConsumers(optional.get().getBroker(), topic);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getConsumerDetail(Long sourceId, String group) {
        Optional<KafkaSource> optional = kafkaSourceRepository.findById(sourceId);
        if (optional.isPresent()) {
            return KafkaUtils.getConsumerDetail(optional.get().getBroker(), group);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void produceMessage(KafkaMessageRequest request) {
        Optional<KafkaSource> optional = kafkaSourceRepository.findById(request.getSourceId());
        if (optional.isPresent()) {
            KafkaUtils.produceMessage(optional.get().getBroker(), request);
        }
    }
}
