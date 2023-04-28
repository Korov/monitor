package org.korov.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author zhu.lei
 * @date 2023-04-16 22:55
 */
@Slf4j
class ZookeeperUtilsTest {

    @Test
    public void getZookeeper() throws IOException, InterruptedException, KeeperException {
        String host = "localhost:2183";
        ZooKeeper zooKeeper = ZookeeperUtils.getZookeeper(host);
        ZooKeeper.States states = zooKeeper.getState();
        log.info("zookeeper states:{}", states);

        int count = zooKeeper.getAllChildrenNumber("/");
        Stat stat = new Stat();
        List<String> childPaths = zooKeeper.getChildren("/zookeeper", false, stat);

        log.info("debug");

        String dataPath = "/data_test";
        ZookeeperUtils.createNode(host, dataPath, "message");
        String data = ZookeeperUtils.getData(host, dataPath);
        log.info("path:{}, data:{}", dataPath, data);

        if (ZookeeperUtils.deleteNode(host, dataPath)) {
            log.info("delete path:{} success", dataPath);
        }

        String noDataPath = "/no_data_test";
        ZookeeperUtils.createNode(host, noDataPath);
        data = ZookeeperUtils.getData(host, noDataPath);
        log.info("path:{}, data:{}", noDataPath, data);

        log.info("debug");
    }

    @Test
    void getZnode() {
    }
}