package org.korov.monitor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.controller.request.TopicRequest;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.repository.KafkaSourceRepository;
import org.korov.monitor.service.KafkaSourceService;
import org.korov.monitor.utils.KafkaUtils;
import org.korov.monitor.vo.Broker;
import org.korov.monitor.vo.Result;
import org.korov.monitor.vo.TopicDescriptionVO;
import org.korov.monitor.vo.TopicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author korov
 */
@Slf4j
@RestController
public class KafkaController {

    private KafkaSourceRepository kafkaSourceRepository;

    @Autowired
    public void setKafkaSourceRepository(KafkaSourceRepository kafkaSourceRepository) {
        this.kafkaSourceRepository = kafkaSourceRepository;
    }

    private KafkaSourceService kafkaSourceService;

    @Autowired
    public void setKafkaSourceService(KafkaSourceService kafkaSourceService) {
        this.kafkaSourceService = kafkaSourceService;
    }

    @PostMapping(value = "/kafka/add")
    public Mono<Result<KafkaSource>> addKafkaSource(@RequestBody KafkaSource kafkaSource) {
        return kafkaSourceService.addKafkaSource(kafkaSource).map(source -> new Result<>(Result.SUCCESS_CODE, null, source));
    }

    @DeleteMapping(value = "/kafka/delete")
    public Mono<Result<KafkaSource>> deleteKafkaSource(@RequestParam(value = "id") Long id) {
        return kafkaSourceService.deleteKafkaSource(id).then(Mono.fromCallable(() -> new Result<>(Result.SUCCESS_CODE, null, null)));
    }

    @GetMapping(value = "/kafka/query")
    public Mono<Result<List<KafkaSource>>> queryKafkaSource() {
        Flux<KafkaSource> kafkaSources = kafkaSourceService.queryAllKafkaSource();
        return kafkaSources.collectSortedList(Comparator.comparing(KafkaSource::getId)).map(list -> new Result<>(Result.SUCCESS_CODE, null, list));
    }

    @GetMapping(value = "/kafka/topic/query")
    public Mono<Result<List<TopicVO>>> queryKafkaTopic(@RequestParam(value = "sourceId") Long sourceId,
                                                 @RequestParam(value = "keyword", required = false) String keyword) {
        return kafkaSourceService.queryTopics(sourceId, keyword).map(list -> new Result<>(Result.SUCCESS_CODE, null, list));
    }

    @GetMapping(value = "/kafka/topic/detail/query")
    public Mono<Result<TopicDescriptionVO>> queryKafkaTopicDetail(@RequestParam(value = "sourceId") Long sourceId,
                                                                  @RequestParam(value = "topic", required = false) String topic) {
        Mono<TopicDescriptionVO> description = kafkaSourceService.queryTopicDetail(sourceId, topic);
        return description.map(value -> new Result<>(Result.SUCCESS_CODE, null, value));
    }

    @PostMapping(value = "/kafka/topic/create")
    public Mono<Result<?>> createTopic(@RequestBody TopicRequest request) throws ExecutionException, InterruptedException {
        return kafkaSourceService.createTopic(request).map(value -> new Result<>(Result.SUCCESS_CODE, null, null));
    }

    @DeleteMapping(value = "/kafka/topic/delete")
    public Mono<Result<?>> createTopic(@RequestParam(value = "sourceId") Long sourceId,
                                 @RequestParam(value = "topic") String topic) {
       return kafkaSourceService.deleteTopic(sourceId, topic).map(value -> new Result<>(Result.SUCCESS_CODE, null, null));
    }

    @GetMapping("/kafka/consumer/query")
    public Mono<Result<List<String>>> getGroupByTopic(@RequestParam(value = "sourceId") Long sourceId,
                                                      @RequestParam(value = "topic", required = false) String topic) {
        Mono<List<String>> consumers = kafkaSourceService.getConsumers(sourceId, topic);
        return consumers.map(values -> new Result<>(Result.SUCCESS_CODE, null, values));
    }

    @GetMapping("/kafka/consumer/detail")
    public Mono<Result<List<Map<String, Object>>>> getGroupDetail(@RequestParam(value = "sourceId") Long sourceId,
                                                                  @RequestParam(value = "group") String group) {
        Mono<List<Map<String, Object>>> consumers = kafkaSourceService.getConsumerDetail(sourceId, group);
        return consumers.map(values -> new Result<>(Result.SUCCESS_CODE, null, values));
    }

    @PostMapping("/kafka/cluster/info")
    public Mono<Result<List<Broker>>> getClusterInfo(@RequestBody KafkaMessageRequest request) {
        return kafkaSourceRepository.findById(request.getSourceId()).map(source -> {
            List<Broker> brokers = KafkaUtils.getClusterInfo(source.getBroker());
            return new Result<>(Result.SUCCESS_CODE, null, brokers);
        });
    }

    @GetMapping(value = "/kafka/addr")
    public Mono<String> getAddr(ServerWebExchange chain) {
        // 获取浏览器访问地址中的ip和端口，防止容器运行时候产生问题
        return Mono.just(chain.getRequest().getURI().getHost() + ":" + chain.getRequest().getURI().getPort());
    }

    @PostMapping("/kafka/message/produce")
    public Mono<Result<?>> produceMessage(@RequestBody KafkaMessageRequest request) {
        return kafkaSourceService.produceMessage(request).map(value -> new Result<>(Result.SUCCESS_CODE, null, null));
    }
}
