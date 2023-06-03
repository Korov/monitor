package org.korov.monitor.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.korov.monitor.MonitorApplicationTests;
import org.korov.monitor.entity.KafkaSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
    void queryTest() throws InterruptedException {
        StepVerifier
                .create(kafkaSourceRepository.findAll())
                .consumeNextWith(source -> log.info("source:{}", source.toString()))
                .expectNextMatches(source -> source.getId() > 0L)
                .verifyComplete();
    }

    @Test
    void exampleQuery() {
        kafkaSourceRepository
                .findAll(Example.of(new KafkaSource(1L, "local", "localhost:9095")))
                .doOnNext(source -> log.info("source:{}", source.toString()));
    }

}