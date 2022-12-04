package org.korov.monitor.controller.request;

import java.util.StringJoiner;

public class ConsumerRequest {
    private String topic;
    private String broker;
    private String group;
    private String reset;
    private Long partition;
    private Long offset;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getReset() {
        return reset;
    }

    public void setReset(String reset) {
        this.reset = reset;
    }

    public Long getPartition() {
        return partition;
    }

    public void setPartition(Long partition) {
        this.partition = partition;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ConsumerRequest.class.getSimpleName() + "[", "]")
                .add("topic='" + topic + "'")
                .add("broker='" + broker + "'")
                .add("group='" + group + "'")
                .add("reset='" + reset + "'")
                .add("partition=" + partition)
                .add("offset=" + offset)
                .toString();
    }
}
