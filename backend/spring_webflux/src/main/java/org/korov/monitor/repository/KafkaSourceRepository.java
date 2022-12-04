package org.korov.monitor.repository;

import org.korov.monitor.entity.KafkaSource;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author korov
 */
public interface KafkaSourceRepository extends ReactiveCrudRepository<KafkaSource, Long> {
}
