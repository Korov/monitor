package org.korov.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhu.lei
 * @date 2023-04-16 22:55
 */
@Slf4j
class ZookeeperUtilsTest {

    @Test
    public void getZookeeper() throws IOException, InterruptedException {

        try (ZooKeeper zooKeeper = ZookeeperUtils.getZookeeper("localhost:2183")) {
            ZooKeeper.States states = zooKeeper.getState();
            log.info("zookeeper states:{}", states);

            int count = zooKeeper.getAllChildrenNumber("/");
            Stat stat = new Stat();
            List<String> childPaths = zooKeeper.getChildren("/zookeeper", false, stat);
            Stat dataStat = new Stat();
            byte[] data = zooKeeper.getData("/zookeeper/config", false, dataStat);
            log.info("debug");

            List<ACL> acl = new ArrayList<>();
            acl.add(new ACL());
            zooKeeper.create("/data_test", "message".getBytes(StandardCharsets.UTF_8), )
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
        log.info("debug");
    }

}