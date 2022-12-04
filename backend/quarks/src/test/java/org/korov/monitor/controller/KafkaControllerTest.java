package org.korov.monitor.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.korov.utils.PrintUtils;

import static io.restassured.RestAssured.given;

/**
 * @author zhu.lei
 * @date 2022-11-25 17:17
 */
@QuarkusTest
class KafkaControllerTest {

    @Test
    void addKafkaSource() {
        given()
                .when()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"test\",\"broker\":\"localhost\"}")
                .post("/kafka/add")
                .then()
                .statusCode(200)
                .body(PrintUtils.print());
    }
}