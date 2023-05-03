package org.korov.monitor.controller;

import lombok.extern.slf4j.Slf4j;
import org.korov.monitor.service.ZookeeperService;
import org.korov.monitor.vo.Result;
import org.korov.monitor.vo.ZNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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

    @GetMapping(value = "/zookeeper/tree")
    public Mono<Result<ZNode>> queryKafkaSource(@RequestParam("host") String host) {
        Mono<ZNode> allNodes = zookeeperService.getZkTree(host);
        return allNodes.map(allNode -> new Result<>(Result.SUCCESS_CODE, null, allNode));
    }
}
