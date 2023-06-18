package org.korov.monitor.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.korov.monitor.MonitorApplicationTests;
import org.korov.monitor.entity.KafkaSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;

import java.util.List;

@Slf4j
class KafkaSourceRepositoryTest extends MonitorApplicationTests {
    private KafkaSourceRepository kafkaSourceRepository;

    @Autowired
    public void setKafkaSourceRepository(KafkaSourceRepository kafkaSourceRepository) {
        this.kafkaSourceRepository = kafkaSourceRepository;
    }

    @Test
    void insertTest() {
        KafkaSource source = new KafkaSource(null, "test", "127.0.0.1:9092");
        kafkaSourceRepository.save(source);
        log.info(source.toString());
    }

    @Test
    void queryTest() {
        StepVerifier
                .create(kafkaSourceRepository.findAll().collectList())
                .consumeNextWith(source -> log.info("source:{}", source.toString()))
                .verifyComplete();
    }

    @Test
    void exampleQuery() {
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<KafkaSource> example = Example.of(new KafkaSource(null, "b", null), matcher);
        StepVerifier.create(kafkaSourceRepository
                        .findAll(example)
                        .collectList()
                ).consumeNextWith(source -> log.info("source:{}", source.toString()))
                .verifyComplete();
    }

    @Test
    void pageQuery() {
        // ExampleMatcher.GenericPropertyMatchers.regex()
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("name", ExampleMatcher.GenericPropertyMatchers.regex());
        Example<KafkaSource> example = Example.of(new KafkaSource(null, "aaa", null), ExampleMatcher.matching());
        StepVerifier.create(kafkaSourceRepository
                        // 表示 offset：1*2， size：2
                        .findAllBy(example, PageRequest.of(1, 2))
                        .collectList()
                ).consumeNextWith(source -> log.info("source:{}", source.toString()))
                .verifyComplete();
    }

    @Test
    void callbackHell() {
        // first map flux to Mono and then block to get the list
        List<KafkaSource> all = kafkaSourceRepository.findAll().collectList().block();
    }

}