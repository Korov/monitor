package org.korov.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ClientInfo;
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
    public void getZookeeper() throws IOException, InterruptedException {
        try (ZooKeeper zooKeeper = ZookeeperUtils.getZookeeper("localhost:2183")) {
            List<ClientInfo> clientInfos = zooKeeper.whoAmI();
            for (ClientInfo clientInfo : clientInfos) {
                log.info(clientInfo.toString());
            }
        }
    }

}