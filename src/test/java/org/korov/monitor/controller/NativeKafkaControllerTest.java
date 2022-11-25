package org.korov.monitor.controller;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.korov.utils.PrintUtils;

import static io.restassured.RestAssured.given;

/**
 * @author zhu.lei
 * @date 2022-11-25 17:17
 */
@QuarkusIntegrationTest
class NativeKafkaControllerTest extends KafkaControllerTest {
}