package org.korov.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ClientInfo;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author zhu.lei
 * @date 2023-04-16 22:55
 */
@Slf4j
public class ZookeeperUtils {

    public static ZooKeeper getZookeeper(String host) throws IOException {
        return new ZooKeeper(host, 20000, null);
    }

    public static List<ClientInfo> getAllClientInfos(String host) throws IOException {
        try (ZooKeeper zooKeeper = ZookeeperUtils.getZookeeper(host)) {
            return zooKeeper.whoAmI();
        } catch (Exception e) {
            log.error("error:{}", host, e);
            return Collections.emptyList();
        }
    }
}
