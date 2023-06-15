package org.korov.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.ClientInfo;
import org.apache.zookeeper.data.Stat;
import org.korov.monitor.vo.ZNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhu.lei
 * @date 2023-04-16 22:55
 */
@Slf4j
public class ZookeeperUtils {
    private static final ConcurrentHashMap<String, ZooKeeper> zooKeeperMap = new ConcurrentHashMap<>();

    public static ZooKeeper getZookeeper(String host) throws IOException {
        ZooKeeper zooKeeper = zooKeeperMap.get(host);
        if (zooKeeper == null) {
            zooKeeper = new ZooKeeper(host, 20000, null);
            zooKeeperMap.put(host, zooKeeper);
            return zooKeeper;
        } else {
            if (!zooKeeper.getState().isAlive()) {
                zooKeeper = new ZooKeeper(host, 20000, null);
            }
            return zooKeeper;
        }
    }

    public static List<ClientInfo> getAllClientInfos(String host) throws IOException {
        ZooKeeper zooKeeper = getZookeeper(host);
        try {
            return zooKeeper.whoAmI();
        } catch (Exception e) {
            log.error("error:{}", host, e);
            return Collections.emptyList();
        }
    }

    public static String createNode(String host, String path) throws IOException, InterruptedException, KeeperException {
        return createNode(host, path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public static String createNode(String host, String path, String data) throws IOException, InterruptedException, KeeperException {
        return createNode(host, path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public static String createNode(String host, String path, String data, List<ACL> acl, CreateMode createMode) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = getZookeeper(host);
        if (zooKeeper.exists(path, false) == null) {
            if (acl == null || acl.isEmpty()) {
                acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
            }
            if (createMode == null) {
                createMode = CreateMode.PERSISTENT;
            }
            if (data == null) {
                return zooKeeper.create(path, null, acl, createMode);
            } else {
                return zooKeeper.create(path, data.getBytes(StandardCharsets.UTF_8), acl, createMode);
            }
        } else {
            log.info("path:{} exists", path);
            return path;
        }
    }

    public static String getData(String host, String path) throws IOException, InterruptedException, KeeperException {
        return getData(host, path, false, null);
    }

    public static String getData(String host, String path, boolean watch, Stat stat) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = getZookeeper(host);
        if (zooKeeper.exists(path, false) == null) {
            return null;
        } else {
            byte[] data = zooKeeper.getData(path, watch, stat);
            if (data == null) {
                return null;
            }
            return new String(data, StandardCharsets.UTF_8);
        }
    }

    public static boolean deleteNode(String host, String path) throws InterruptedException, KeeperException, IOException {
        return deleteNode(host, path, -1);
    }

    public static boolean deleteNode(String host, String path, Integer version) throws InterruptedException, KeeperException, IOException {
        ZooKeeper zooKeeper = getZookeeper(host);
        if (version == null) {
            version = -1;
        }
        zooKeeper.delete(path, version);
        return zooKeeper.exists(path, false) == null;
    }

    public static ZNode getZnode(String host, String path) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = getZookeeper(host);
        if (zooKeeper.exists(path, false) == null) {
            return null;
        }
        Stat stat = new Stat();
        String data = getData(host, path, false, stat);
        ZNode zNode = new ZNode();
        zNode.setPath(path);
        zNode.setStat(stat);
        zNode.setData(data);
        return zNode;
    }

    public static ZNode getAllZnode(String host) {
        return getAllZnode(host, "/", true);
    }

    public static ZNode getAllZnode(String host, String path, Boolean recursion) {
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        if (recursion == null) {
            recursion = true;
        }
        try {
            ZooKeeper zooKeeper = getZookeeper(host);
            if (zooKeeper.exists(path, false) == null) {
                return null;
            }
            Stat stat = new Stat();
            String data = getData(host, path, false, stat);
            ZNode parentNode = new ZNode();
            parentNode.setPath(path);
            parentNode.setStat(stat);
            parentNode.setData(data);
            if (!recursion) {
                return parentNode;
            }

            List<ZNode> childNodes = new ArrayList<>();
            List<String> childPaths = zooKeeper.getChildren(path, false, null);
            if (childPaths != null && !childPaths.isEmpty()) {
                for (String childPath : childPaths) {
                    ZNode childNode;
                    if (Objects.equals("/", path)) {
                        childNode = getAllZnode(host, "/" + childPath, recursion);
                    } else {
                        childNode = getAllZnode(host, path + "/" + childPath, recursion);
                    }
                    if (childNode != null) {
                        childNode.setParentPath(path);
                        childNodes.add(childNode);
                    }
                }
            }
            parentNode.setChildNodes(childNodes);
            return parentNode;
        } catch (Exception e) {
            log.error("get all znode failed", e);
        }
        return null;
    }
}
