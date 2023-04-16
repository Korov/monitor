package org.korov.monitor.utils;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * @author zhu.lei
 * @date 2023-04-16 22:55
 */
public class ZookeeperUtils {
    public static ZooKeeper getZookeeper(String host) throws IOException {
        return new ZooKeeper(host, 20000, null);
    }
}
