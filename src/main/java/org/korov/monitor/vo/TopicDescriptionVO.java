package org.korov.monitor.vo;

import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author korov
 */
public class TopicDescriptionVO {
    private String name;
    private Boolean internal;

    private List<PartitionInfo> partitions;

    public TopicDescriptionVO() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public List<PartitionInfo> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<PartitionInfo> partitions) {
        this.partitions = partitions;
    }

    public TopicDescriptionVO(TopicDescription description) {
        this.name = description.name();
        this.internal = description.isInternal();
        if (description.partitions() != null) {
            List<PartitionInfo> partitions = new ArrayList<>(description.partitions().size());
            for (TopicPartitionInfo topicPartitionInfo : description.partitions()) {
                PartitionInfo partitionInfo = new PartitionInfo();
                partitionInfo.setPartition(topicPartitionInfo.partition());
                partitionInfo.setLeader(new Node(topicPartitionInfo.leader()));
                if (topicPartitionInfo.replicas() != null) {
                    List<Node> replicas = new ArrayList<>(topicPartitionInfo.replicas().size());
                    for (org.apache.kafka.common.Node node : topicPartitionInfo.replicas()) {
                        replicas.add(new Node(node));
                    }
                    partitionInfo.setReplicas(replicas);
                }

                if (topicPartitionInfo.isr() != null) {
                    List<Node> isr = new ArrayList<>(topicPartitionInfo.isr().size());
                    for (org.apache.kafka.common.Node node : topicPartitionInfo.isr()) {
                        isr.add(new Node(node));
                    }
                    partitionInfo.setIsr(isr);
                }
                partitions.add(partitionInfo);
            }
            this.partitions = partitions;
        }
    }


    public class PartitionInfo {
        private int partition;
        private Node leader;
        private List<Node> replicas;
        private List<Node> isr;
        private Long beginningOffset;
        private Long endOffset;

        public int getPartition() {
            return partition;
        }

        public void setPartition(int partition) {
            this.partition = partition;
        }

        public Node getLeader() {
            return leader;
        }

        public void setLeader(Node leader) {
            this.leader = leader;
        }

        public List<Node> getReplicas() {
            return replicas;
        }

        public void setReplicas(List<Node> replicas) {
            this.replicas = replicas;
        }

        public List<Node> getIsr() {
            return isr;
        }

        public void setIsr(List<Node> isr) {
            this.isr = isr;
        }

        public Long getBeginningOffset() {
            return beginningOffset;
        }

        public void setBeginningOffset(Long beginningOffset) {
            this.beginningOffset = beginningOffset;
        }

        public Long getEndOffset() {
            return endOffset;
        }

        public void setEndOffset(Long endOffset) {
            this.endOffset = endOffset;
        }
    }

    public class Node {
        private int id;
        private String host;
        private int port;
        private String rack;

        public Node() {

        }

        public Node(org.apache.kafka.common.Node node) {
            this.id = node.id();
            this.host = node.host();
            this.port = node.port();
            this.rack = node.rack();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getRack() {
            return rack;
        }

        public void setRack(String rack) {
            this.rack = rack;
        }
    }
}
