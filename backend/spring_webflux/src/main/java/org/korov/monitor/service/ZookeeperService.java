package org.korov.monitor.service;

import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.entity.ZookeeperSource;
import org.korov.monitor.vo.ZNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author korov
 */
public interface ZookeeperService {
    Mono<ZookeeperSource> addZookeeperSource(ZookeeperSource zookeeperSource);

    Mono<Void> deleteZookeeperSource(Long id);

    Flux<ZookeeperSource> queryAllZookeeperSource();

    Mono<ZNode> getZkTree(String host);
}
