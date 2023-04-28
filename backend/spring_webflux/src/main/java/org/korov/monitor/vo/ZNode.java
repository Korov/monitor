package org.korov.monitor.vo;

import lombok.Data;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @author zhu.lei
 * @date 2023-04-28 11:37
 */
@Data
public class ZNode {
    private String parentPath;
    private String path;
    private Stat stat;
    private String data;

    private List<ZNode> childNodes;
}
