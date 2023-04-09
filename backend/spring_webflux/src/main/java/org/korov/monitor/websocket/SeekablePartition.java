package org.korov.monitor.websocket;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import reactor.kafka.receiver.ReceiverPartition;

import java.util.Collections;
import java.util.Map;

/**
 * @author zhu.lei
 * @date 2023-04-09 22:14
 */
class SeekablePartition implements ReceiverPartition {

    private final Consumer<?, ?> consumer;
    private final TopicPartition topicPartition;

    public SeekablePartition(Consumer<?, ?> consumer, TopicPartition topicPartition) {
        this.consumer = consumer;
        this.topicPartition = topicPartition;
    }

    @Override
    public TopicPartition topicPartition() {
        return topicPartition;
    }

    @Override
    public void seekToBeginning() {
        this.consumer.seekToBeginning(Collections.singletonList(topicPartition));
    }

    @Override
    public void seekToEnd() {
        this.consumer.seekToEnd(Collections.singletonList(topicPartition));
    }

    @Override
    public void seek(long offset) {
        this.consumer.seek(topicPartition, offset);
    }

    @Override
    public void seekToTimestamp(long timestamp) {
        Map<TopicPartition, OffsetAndTimestamp> offsets = this.consumer
                .offsetsForTimes(Collections.singletonMap(this.topicPartition, timestamp));
        OffsetAndTimestamp next = offsets.values().iterator().next();
        if (next == null) {
            seekToEnd();
        } else {
            this.consumer.seek(this.topicPartition, next.offset());
        }
    }

    @Override
    public long position() {
        return this.consumer.position(topicPartition);
    }

    @Override
    public String toString() {
        return String.valueOf(topicPartition);
    }
}
