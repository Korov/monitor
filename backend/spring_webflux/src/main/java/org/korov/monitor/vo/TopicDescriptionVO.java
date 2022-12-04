package org.korov.monitor.vo;

import lombok.Data;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author korov
 */
@Data
public class TopicDescriptionVO {
    private String name;
    private Boolean internal;

    private List<PartitionInfo> partitions;

    public TopicDescriptionVO() {

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

    @Data
    public class PartitionInfo {
        private int partition;
        private Node leader;
        private List<Node> replicas;
        private List<Node> isr;
        private Long beginningOffset;
        private Long endOffset;
    }

    @Data
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
    }
}
