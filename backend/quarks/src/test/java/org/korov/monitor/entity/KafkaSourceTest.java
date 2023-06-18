package org.korov.monitor.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author korov
 */
@QuarkusTest
class KafkaSourceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSourceTest.class);

    @Test
    void callbackHell() {
        // first transform PanacheQuery to Uni then await the result
        List<PanacheEntityBase> all = KafkaSource.findAll().list().await().indefinitely();
        LOGGER.info("debug");
    }
}