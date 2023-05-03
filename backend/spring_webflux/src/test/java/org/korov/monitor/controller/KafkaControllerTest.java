package org.korov.monitor.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.korov.monitor.MonitorApplicationTests;
import org.korov.monitor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
class KafkaControllerTest extends MonitorApplicationTests {

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"test112\",\"broker\":\"127.0.0.1:9092\"}"
    })
    void addKafkaSource(String content) {
        FluxExchangeResult<Result> result = webClient.post().uri("/kafka/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(content).exchange().expectStatus().isOk()
                .returnResult(Result.class);
        log.info(result.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2"
    })
    void deleteKafkaSource(String content) {
        FluxExchangeResult<Result> result = webClient.delete().uri(String.format("/kafka/delete?id=%s", content))
                .exchange().expectStatus().isOk()
                .returnResult(Result.class);
        log.info(result.toString());
    }

    @Test
    void queryKafkaSource() {
        FluxExchangeResult<Result> result = webClient.get().uri("/kafka/query").exchange().expectStatus().isOk()
                .returnResult(Result.class);
        log.info(result.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1"
    })
    void queryKafkaTopic(String content) {
        FluxExchangeResult<Result> result = webClient.get().uri(String.format("/kafka/topic/query?sourceId=%s", content))
                .exchange().expectStatus().isOk()
                .returnResult(Result.class);
        log.info(result.toString());
    }

    @Test
    void queryKafkaTopicDetail() {
        FluxExchangeResult<Result> result = webClient.get().uri("/kafka/topic/detail/query?sourceId=1&topic=monitor_topic")
                .exchange().expectStatus().isOk()
                .returnResult(Result.class);
        log.info(result.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"sourceId\":2,\"topic\":\"monitor_topic\",\"key\":\"aaa\",\"message\":\"testssss\",\"partition\":2}"
    })
    void produceMessage(String content) {
        FluxExchangeResult<Result> result = webClient.post().uri("/kafka/message/produce")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(content)
                .exchange().expectStatus().isOk()
                .returnResult(Result.class);
        log.info(result.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"sourceId\":1}"
    })
    void getClusterInfo(String content) {
        FluxExchangeResult<Result> result = webClient.post().uri("/kafka/cluster/info")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(content)
                .exchange().expectStatus().isOk()
                .returnResult(Result.class);
        log.info(result.toString());
    }
}