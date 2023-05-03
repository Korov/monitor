package org.korov.monitor.service.impl;

import org.korov.monitor.service.ZookeeperService;
import org.korov.monitor.utils.ZookeeperUtils;
import org.korov.monitor.vo.ZNode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author korov
 */
@Service
public class ZookeeperServiceImpl implements ZookeeperService {
    @Override
    public Mono<ZNode> getZkTree(String host) {
        Mono<String> hostMono = Mono.just(host);
        return hostMono.map(zkHost -> ZookeeperUtils.getAllZnode(zkHost));
    }
}
