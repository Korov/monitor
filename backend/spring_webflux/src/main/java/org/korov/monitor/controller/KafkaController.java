package org.korov.monitor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.controller.request.TopicRequest;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.service.KafkaSourceService;
import org.korov.monitor.vo.Result;
import org.korov.monitor.vo.TopicDescriptionVO;
import org.korov.monitor.vo.TopicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author korov
 */
@Slf4j
@RestController
public class KafkaController {
    private KafkaSourceService kafkaSourceService;

    @Autowired
    public void setKafkaSourceService(KafkaSourceService kafkaSourceService) {
        this.kafkaSourceService = kafkaSourceService;
    }

    @PostMapping(value = "/kafka/add")
    public Result<Mono<KafkaSource>> addKafkaSource(@RequestBody KafkaSource kafkaSource) {
        return new Result<>(Result.SUCCESS_CODE, null, kafkaSourceService.addKafkaSource(kafkaSource));
    }

    @DeleteMapping(value = "/kafka/delete")
    public Result<KafkaSource> deleteKafkaSource(@RequestParam(value = "id") Long id) {
        kafkaSourceService.deleteKafkaSource(id);
        return new Result<>(Result.SUCCESS_CODE, null, null);
    }

    @GetMapping(value = "/kafka/query")
    public Result<Flux<KafkaSource>> queryKafkaSource() {
        Flux<KafkaSource> kafkaSources = kafkaSourceService.queryAllKafkaSource();
        return new Result<>(Result.SUCCESS_CODE, null, kafkaSources);
    }

    @GetMapping(value = "/kafka/topic/query")
    public Result<Flux<TopicVO>> queryKafkaTopic(@RequestParam(value = "sourceId") Long sourceId,
                                                 @RequestParam(value = "keyword", required = false) String keyword) {
        Flux<TopicVO> topics = kafkaSourceService.queryTopics(sourceId, keyword);
        return new Result<>(Result.SUCCESS_CODE, null, topics);
    }

    @GetMapping(value = "/kafka/topic/detail/query")
    public Result<Mono<TopicDescriptionVO>> queryKafkaTopicDetail(@RequestParam(value = "sourceId") Long sourceId,
                                                            @RequestParam(value = "topic") String topic) throws JsonProcessingException {
        TopicDescriptionVO description = kafkaSourceService.queryTopicDetail(sourceId, topic);
        return new Result<>(Result.SUCCESS_CODE, null, description);
    }

    @PostMapping(value = "/kafka/topic/create")
    public Result<?> createTopic(@RequestBody TopicRequest request) throws ExecutionException, InterruptedException {
        kafkaSourceService.createTopic(request);

        return new Result<>(Result.SUCCESS_CODE, null, null);
    }

    @DeleteMapping(value = "/kafka/topic/delete")
    public Result<?> createTopic(@RequestParam(value = "sourceId") Long sourceId,
                                 @RequestParam(value = "topic") String topic) {
        kafkaSourceService.deleteTopic(sourceId, topic);
        return new Result<>(Result.SUCCESS_CODE, null, null);
    }

    @GetMapping("/kafka/consumer/query")
    public Result<?> getGroupByTopic(@RequestParam(value = "sourceId") Long sourceId,
                                     @RequestParam(value = "topic") String topic) {
        List<Map<String, Object>> consumers = kafkaSourceService.getConsumers(sourceId, topic);
        return new Result<>(Result.SUCCESS_CODE, null, consumers);
    }

    @GetMapping("/kafka/consumer/detail")
    public Result<?> getGroupDetail(@RequestParam(value = "sourceId") Long sourceId,
                                    @RequestParam(value = "group") String group) {
        List<Map<String, Object>> consumers = kafkaSourceService.getConsumerDetail(sourceId, group);
        return new Result<>(Result.SUCCESS_CODE, null, consumers);
    }

    @GetMapping(value = "/kafka/addr")
    public String getAddr(ServerWebExchange chain) {
        //获取浏览器访问地址中的ip和端口，防止容器运行时候产生问题
        return chain.getRequest().getURI().getHost() + ":" + chain.getRequest().getURI().getPort();
    }

    @PostMapping("/kafka/message/produce")
    public Result<?> produceMessage(@RequestBody KafkaMessageRequest request) {
        kafkaSourceService.produceMessage(request);
        return new Result<>(Result.SUCCESS_CODE, null, null);
    }
}
