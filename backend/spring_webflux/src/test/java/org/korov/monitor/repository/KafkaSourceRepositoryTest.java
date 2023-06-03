package org.korov.monitor.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.korov.monitor.MonitorApplicationTests;
import org.korov.monitor.entity.KafkaSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;

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
        StepVerifier.create(kafkaSourceRepository
                        .findAll(Example.of(new KafkaSource(null, "other", null)))
                        .collectList()
                ).consumeNextWith(source -> log.info("source:{}", source.toString()))
                .verifyComplete();
    }

    @Test
    void pageQuery() {
        StepVerifier.create(kafkaSourceRepository
                        // 表示 offset：1*2， size：2
                        .findAllBy(PageRequest.of(1, 2))
                        .collectList()
                ).consumeNextWith(source -> log.info("source:{}", source.toString()))
                .verifyComplete();
    }

}