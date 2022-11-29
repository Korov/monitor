package org.korov.monitor.utils;

import org.junit.jupiter.api.Test;

class KafkaUtilsTest {

    @Test
    void queryTopics() {
        KafkaUtils.queryTopics("localhost:9095", "");
    }
}