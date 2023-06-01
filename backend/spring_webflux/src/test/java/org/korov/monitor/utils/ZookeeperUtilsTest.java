package org.korov.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeperMain;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import org.korov.monitor.vo.ZNode;

import java.io.IOException;
import java.util.List;

/**
 * @author korov
 */
@Slf4j
class ZookeeperUtilsTest {
    private static final String host = "localhost:2183";

    @Test
    public void getZookeeper() throws IOException, InterruptedException, KeeperException {
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

        String noDataPath = "/no_data_test";
        ZookeeperUtils.createNode(host, noDataPath);
        data = ZookeeperUtils.getData(host, noDataPath);
        log.info("path:{}, data:{}", noDataPath, data);

        log.info("debug");
    }

    @Test
    void getZnode() throws IOException, InterruptedException, KeeperException {
        ZNode zNode = ZookeeperUtils.getZnode(host, "/data_test");
        log.info("znode:{}", zNode);
    }

    @Test
    void getAllZnode() throws IOException, InterruptedException, KeeperException {
        ZNode allNode = ZookeeperUtils.getAllZnode("192.168.1.19:2181");
        log.info("debug");
    }

    @Test
    void testMain() throws IOException, InterruptedException {
        ZooKeeperMain zooKeeperMain = new ZooKeeperMain(ZookeeperUtils.getZookeeper(host));
        log.info("debug");
        zooKeeperMain.executeLine("mntr");
        log.info("debug");
    }
}