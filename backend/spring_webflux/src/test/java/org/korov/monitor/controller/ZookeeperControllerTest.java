package org.korov.monitor.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.korov.monitor.MonitorApplicationTests;
import org.korov.monitor.vo.Result;
import org.springframework.test.web.reactive.server.FluxExchangeResult;

/**
 * @author korov
 */
@Slf4j
class ZookeeperControllerTest extends MonitorApplicationTests {

    @ParameterizedTest
    @ValueSource(strings = {
            "localhost:2183"
    })
    void queryKafkaSource(String content) {
        FluxExchangeResult<Result> result = webClient.get().uri(String.format("/zookeeper/tree?host=%s", content))
                .exchange().expectStatus().isOk()
                .returnResult(Result.class);
        log.info(result.toString());
    }
}