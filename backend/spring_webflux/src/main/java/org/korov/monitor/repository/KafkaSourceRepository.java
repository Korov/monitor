package org.korov.monitor.repository;

import org.korov.monitor.entity.KafkaSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

/**
 * @author korov
 */
public interface KafkaSourceRepository extends R2dbcRepository<KafkaSource, Long> {
    Flux<KafkaSource> findAllBy(Pageable pageable);
}
