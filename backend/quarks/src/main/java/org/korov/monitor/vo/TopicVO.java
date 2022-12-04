package org.korov.monitor.vo;


import com.google.common.base.MoreObjects;

import javax.persistence.Entity;

/**
 * @author korov
 */
public class TopicVO {
    String name;
    boolean isInternal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public void setInternal(boolean internal) {
        isInternal = internal;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("isInternal", isInternal)
                .toString();
    }
}
