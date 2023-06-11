package org.korov.monitor.vo;

import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @author korov
 */
public class ZNode {
    private String parentPath;
    private String path;
    private Stat stat;
    private String data;

    private List<ZNode> childNodes;

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<ZNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<ZNode> childNodes) {
        this.childNodes = childNodes;
    }
}
