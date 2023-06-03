package org.korov.monitor.service.impl;

import org.korov.monitor.entity.ZookeeperSource;
import org.korov.monitor.repository.ZookeeperSourceRepository;
import org.korov.monitor.service.ZookeeperService;
import org.korov.monitor.utils.ZookeeperUtils;
import org.korov.monitor.vo.ZNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author korov
 */
@Service
public class ZookeeperServiceImpl implements ZookeeperService {
    private ZookeeperSourceRepository zookeeperSourceRepository;

    @Autowired
    public void setZookeeperSourceRepository(ZookeeperSourceRepository zookeeperSourceRepository) {
        this.zookeeperSourceRepository = zookeeperSourceRepository;
    }

    @Override
    public Mono<ZookeeperSource> addZookeeperSource(ZookeeperSource zookeeperSource) {
        return zookeeperSourceRepository.save(zookeeperSource);
    }

    @Override
    public Mono<Void> deleteZookeeperSource(Long id) {
        return zookeeperSourceRepository.deleteById(id);
    }

    @Override
    public Flux<ZookeeperSource> queryAllZookeeperSource() {
        return zookeeperSourceRepository.findAll();
    }

    @Override
    public Mono<ZNode> getZkTree(String host) {
        Mono<String> hostMono = Mono.just(host);
        return hostMono.map(zkHost -> ZookeeperUtils.getAllZnode(zkHost));
    }
}
