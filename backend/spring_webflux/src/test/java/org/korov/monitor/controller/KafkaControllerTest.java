package org.korov.monitor.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.korov.monitor.MonitorApplicationTests;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Slf4j
class KafkaControllerTest extends MonitorApplicationTests {
    protected WebTestClient webClient;

    @Autowired
    public void setWebClient(WebTestClient webClient) {
        this.webClient = webClient;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"test112\",\"broker\":\"127.0.0.1:9092\"}"
    })
    void addKafkaSource(String content) throws Exception {
        FluxExchangeResult<Result> result = webClient.post().uri("/kafka/add").contentType(MediaType.APPLICATION_JSON)
                .body(content, KafkaSource.class).exchange().expectStatus().isOk()
                .returnResult(Result.class);
        log.info(result.toString());
    }

    @Test
    void queryKafkaSource() {
    }

    /*@ParameterizedTest
    @ValueSource(strings = {
            "2"
    })
    void deleteKafkaSource(String content) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/kafka/delete?id=%s", content))
                        .content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
        log.info(response);
    }

    @Test
    void queryKafkaSource() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/kafka/query").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
        log.info(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1"
    })
    void queryKafkaTopic(String content) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/kafka/topic/query?sourceId=%s", content))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
        log.info(response);
    }

    @Test
    void queryKafkaTopicDetail() throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/kafka/topic/detail/query?sourceId=1&topic=monitor_topic")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
        log.info(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"sourceId\":2,\"topic\":\"monitor_topic\",\"key\":\"aaa\",\"message\":\"testssss\",\"partition\":2}"
    })
    void produceMessage(String content) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/kafka/message/produce").content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
        log.info(response);
    }*/
}