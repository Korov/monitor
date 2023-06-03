package org.korov.monitor.repository;

import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.entity.ZookeeperSource;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author korov
 */
public interface ZookeeperSourceRepository extends ReactiveCrudRepository<ZookeeperSource, Long> {
}
