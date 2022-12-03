package org.korov.monitor.utils;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.vo.Broker;
import org.korov.monitor.vo.TopicDescriptionVO;
import org.korov.monitor.vo.TopicVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author korov
 */
public class KafkaUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUtils.class);

    public static List<TopicVO> queryTopics(String broker, String keyword) {
        try (AdminClient adminClient = getClient(broker)) {
            ListTopicsOptions options = new ListTopicsOptions();
            // 列出内部的Topic
            options.listInternal(true);

            // 列出所有的topic
            ListTopicsResult result = adminClient.listTopics(options);
            Collection<TopicListing> topicListings = new HashSet<>();
            try {
                topicListings = result.listings().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            List<TopicVO> collect = topicListings.stream().map(t -> {
                TopicVO topic = new TopicVO();
                topic.setName(t.name());
                topic.setInternal(t.isInternal());
                return topic;
            }).sorted(Comparator.comparing(TopicVO::getName)).collect(Collectors.toList());

            if (keyword != null) {
                collect = collect.stream().filter(t -> t.getName().contains(keyword)).collect(Collectors.toList());
            }
            return collect;
        }
    }

    public static TopicDescriptionVO getTopicDetail(String broker, String topic) {
        Map<String, TopicDescriptionVO> map = getTopicsDetail(broker, Collections.singleton(topic));
        return map.get(topic);
    }

    public static List<TopicPartition> getTopicPartitions(String broker, String topic) {
        try (AdminClient adminClient = getClient(broker)) {
            DescribeTopicsResult topicsResult = adminClient.describeTopics(Collections.singleton(topic));
            List<TopicPartition> topicPartitions = new ArrayList<>();
            for (Map.Entry<String, TopicDescription> entry : topicsResult.all().get().entrySet()) {
                for (TopicPartitionInfo partitionInfo : entry.getValue().partitions()) {
                    topicPartitions.add(new TopicPartition(topic, partitionInfo.partition()));
                }
            }
            return topicPartitions;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static Map<String, TopicDescriptionVO> getTopicsDetail(String broker, Set<String> topics) {
        try (AdminClient adminClient = getClient(broker); KafkaConsumer<String, String> consumer = getConsumer(broker)) {
            DescribeTopicsResult topicsResult = adminClient.describeTopics(topics);
            Map<String, TopicDescriptionVO> map = new HashMap<>();
            try {
                for (Map.Entry<String, TopicDescription> entry : topicsResult.all().get().entrySet()) {
                    map.put(entry.getKey(), new TopicDescriptionVO(entry.getValue()));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            List<TopicPartition> topicPartitions = new ArrayList<>();
            for (Map.Entry<String, TopicDescriptionVO> entry : map.entrySet()) {
                String topic = entry.getKey();
                TopicDescriptionVO description = entry.getValue();
                if (description.getPartitions() != null) {
                    for (TopicDescriptionVO.PartitionInfo partition : description.getPartitions()) {
                        topicPartitions.add(new TopicPartition(topic, partition.getPartition()));
                    }
                }
            }
            consumer.subscribe(topics);
            Map<TopicPartition, Long> endOffsetMap = consumer.endOffsets(topicPartitions);
            Map<TopicPartition, Long> beginningOffsetMap = consumer.beginningOffsets(topicPartitions);

            for (Map.Entry<String, TopicDescriptionVO> entry : map.entrySet()) {
                String topic = entry.getKey();
                TopicDescriptionVO description = entry.getValue();
                if (description.getPartitions() != null) {
                    for (TopicDescriptionVO.PartitionInfo partition : description.getPartitions()) {
                        TopicPartition topicPartition = new TopicPartition(topic, partition.getPartition());
                        partition.setBeginningOffset(beginningOffsetMap.get(topicPartition));
                        partition.setEndOffset(endOffsetMap.get(topicPartition));
                    }
                }
            }
            return map;
        }
    }

    public static void createTopic(String broker, String topic, int partition, int replica) throws ExecutionException, InterruptedException {
        try (AdminClient adminClient = getClient(broker)) {
            List<NewTopic> newTopics = new ArrayList<>();
            NewTopic newTopic = new NewTopic(topic, partition, (short) replica);
            newTopics.add(newTopic);
            CreateTopicsResult result = adminClient.createTopics(newTopics);
            result.all().get();
            result.values().forEach((name, future) -> System.out.println("topicName:" + name));
        }
    }

    public static void deleteTopic(String broker, String topic) {
        try (AdminClient adminClient = getClient(broker)) {
            adminClient.deleteTopics(Collections.singleton(topic));
        }
    }

    public static List<String> getConsumers(String broker, String topic) {
        try (AdminClient adminClient = getClient(broker)) {
            return adminClient.listConsumerGroups().all().get().parallelStream().map(ConsumerGroupListing::groupId).filter(groupId -> {
                try {
                    return adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get().keySet()
                            .stream().anyMatch(p -> {
                                if (topic == null || topic.isEmpty()) {
                                    return true;
                                } else {
                                    return p.topic().equals(topic);
                                }
                            });
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return false;
            }).collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static List<Map<String, Object>> getConsumerDetail(String broker, String group) {
        try (AdminClient adminClient = getClient(broker)) {
            ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(group);
            Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();
            Set<TopicPartition> topicPartitions = topicPartitionOffsetAndMetadataMap.keySet();
            Set<String> topics = topicPartitions.stream().map(TopicPartition::topic).collect(Collectors.toSet());
            try (KafkaConsumer<String, String> consumer = getConsumer(broker, group)) {
                consumer.subscribe(topics);
                Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
                return topicPartitions.stream().map(t -> {
                    OffsetAndMetadata offsetAndMetadata = topicPartitionOffsetAndMetadataMap.get(t);
                    long offset = offsetAndMetadata.offset();
                    Map<String, Object> jsonObject = new HashMap<>();
                    jsonObject.put("topic", t.topic());
                    jsonObject.put("partition", t.partition());
                    jsonObject.put("offset", offset);
                    TopicPartition topicPartition = new TopicPartition(t.topic(), t.partition());
                    Long endOffset = endOffsets.get(topicPartition);
                    jsonObject.put("endOffset", endOffset);
                    jsonObject.put("lag", endOffset - offset);
                    return jsonObject;
                }).sorted(Comparator.comparing(KafkaUtils::comparingByName).thenComparing(KafkaUtils::comparingByPartition)).collect(Collectors.toList());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static String comparingByName(Map<String, Object> jo) {
        return (String) jo.get("topic");
    }

    private static Integer comparingByPartition(Map<String, Object> jo) {
        return (Integer) jo.get("partition");
    }

    public static AdminClient getClient(String broker) {
        Properties prop = new Properties();

        prop.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        prop.setProperty(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "20000");
        prop.setProperty(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "20000");
        return AdminClient.create(prop);
    }

    private static KafkaConsumer<String, String> getConsumer(String broker) {
        return getConsumer(broker, "monitor");
    }

    public static KafkaConsumer<String, String> getConsumer(String broker, String group) {
        return getConsumer(broker, group, "latest");
    }

    public static KafkaConsumer<String, String> getConsumer(String broker, String group, String offset) {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offset);
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    public static void produceMessage(String broker, KafkaMessageRequest request) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        StringSerializer stringSerializer = new StringSerializer();

        try (Producer<String, String> producer = new KafkaProducer<>(props, stringSerializer, stringSerializer)) {
            Future<RecordMetadata> send = producer.send(new ProducerRecord<>(request.getTopic(), request.getPartition(), System.currentTimeMillis(), request.getKey(), request.getMessage()));
            send.get(3, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void produceMessage(String broker, String topic, String key, String message) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        StringSerializer stringSerializer = new StringSerializer();

        try (Producer<String, String> producer = new KafkaProducer<>(props, stringSerializer, stringSerializer)) {
            Future<RecordMetadata> send = producer.send(new ProducerRecord<>(topic, key, message));
            send.get(3, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static List<Broker> getClusterInfo(String broker) {
        try (AdminClient adminClient = getClient(broker)) {
            Collection<Node> clusters = adminClient.describeCluster().nodes().get();
            List<Broker> brokers = new ArrayList<>(clusters.size());
            for (Node cluster : clusters) {
                brokers.add(new Broker(cluster.id(), cluster.host(), cluster.port()));
            }
            return brokers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
