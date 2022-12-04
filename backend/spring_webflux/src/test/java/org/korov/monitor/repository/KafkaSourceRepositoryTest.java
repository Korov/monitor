package org.korov.monitor.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.korov.monitor.MonitorApplicationTests;
import org.korov.monitor.entity.KafkaSource;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class KafkaSourceRepositoryTest extends MonitorApplicationTests {
    @Autowired
    private KafkaSourceRepository kafkaSourceRepository;

    @Test
    void insertTest() {
        KafkaSource source = new KafkaSource(null, "test", "127.0.0.1:9092");
        kafkaSourceRepository.save(source);
        log.info(source.toString());
    }

    @Test
    void queryTest() {
        Flux<KafkaSource> flux = kafkaSourceRepository.findAll();
        flux.doFirst(System.out::println);
    }

}