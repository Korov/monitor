package org.korov.monitor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.korov.monitor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
public class MonitorApplicationTests {
    protected WebTestClient webClient;

    @Autowired
    public void setWebClient(WebTestClient webClient) {
        this.webClient = webClient;
    }

    @Test
    void contextLoads() {
        log.info("success");
    }
}
