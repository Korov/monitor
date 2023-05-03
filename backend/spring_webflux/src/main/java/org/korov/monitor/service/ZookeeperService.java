package org.korov.monitor.service;

import org.korov.monitor.vo.ZNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author korov
 */
public interface ZookeeperService {
    Mono<ZNode> getZkTree(String host);
}
