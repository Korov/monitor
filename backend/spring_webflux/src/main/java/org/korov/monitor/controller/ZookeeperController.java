package org.korov.monitor.controller;

import lombok.extern.slf4j.Slf4j;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.entity.ZookeeperSource;
import org.korov.monitor.service.ZookeeperService;
import org.korov.monitor.vo.Result;
import org.korov.monitor.vo.ZNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

/**
 * @author korov
 */
@Slf4j
@RestController
public class ZookeeperController {

    private ZookeeperService zookeeperService;

    @Autowired
    public void setZookeeperService(ZookeeperService zookeeperService) {
        this.zookeeperService = zookeeperService;
    }

    @PostMapping(value = "/zookeeper/address/add")
    public Mono<Result<ZookeeperSource>> addZookeeperSource(@RequestBody ZookeeperSource zookeeperSource) {
        return zookeeperService.addZookeeperSource(zookeeperSource).map(source -> new Result<>(Result.SUCCESS_CODE, null, source));
    }

    @DeleteMapping(value = "/zookeeper/address/del")
    public Mono<Result<ZookeeperSource>> deleteKafkaSource(@RequestParam(value = "id") Long id) {
        return zookeeperService.deleteZookeeperSource(id).then(Mono.fromCallable(() -> new Result<>(Result.SUCCESS_CODE, null, null)));
    }

    @GetMapping(value = "/zookeeper/zookeeper/query")
    public Mono<Result<List<ZookeeperSource>>> queryKafkaSource() {
        Flux<ZookeeperSource> zookeeperSources = zookeeperService.queryAllZookeeperSource();
        return zookeeperSources.collectSortedList(Comparator.comparing(ZookeeperSource::getId)).map(list -> new Result<>(Result.SUCCESS_CODE, null, list));
    }

    @GetMapping(value = "/zookeeper/tree")
    public Mono<Result<ZNode>> queryKafkaSource(@RequestParam("host") String host) {
        Mono<ZNode> allNodes = zookeeperService.getZkTree(host);
        return allNodes.map(allNode -> new Result<>(Result.SUCCESS_CODE, null, allNode));
    }
}
