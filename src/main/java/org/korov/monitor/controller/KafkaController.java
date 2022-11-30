package org.korov.monitor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import org.korov.monitor.controller.request.KafkaMessageRequest;
import org.korov.monitor.controller.request.TopicRequest;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.utils.KafkaUtils;
import org.korov.monitor.vo.Broker;
import org.korov.monitor.vo.Result;
import org.korov.monitor.vo.TopicDescriptionVO;
import org.korov.monitor.vo.TopicVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author zhu.lei
 * @date 2022-01-14 17:44
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KafkaController {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaController.class);

    @Path("/kafka/add")
    @POST
    public Uni<Result> addKafkaSource(KafkaSource kafkaSource) {
        return Panache.withTransaction(kafkaSource::persist).replaceWith(kafkaSource).onItem()
                .transform(source -> new Result(Result.SUCCESS_CODE, null, source));
    }

    @Path(value = "/kafka/delete")
    @DELETE
    public Uni<Result> deleteKafkaSource(@QueryParam("id") Long id) {
        return Panache.withTransaction(() -> KafkaSource.deleteById(id)).onItem()
                .transform((deleted) -> new Result(Result.SUCCESS_CODE, null, deleted));
    }

    @Path(value = "/kafka/query")
    @GET
    public Uni<Result> queryKafkaSource() {
        return KafkaSource.listAll().onItem().transform(list -> new Result(Result.SUCCESS_CODE, null, list));
    }

    @Path(value = "/kafka/topic/query")
    @GET
    public Uni<Result> queryKafkaTopic(@QueryParam(value = "sourceId") Long sourceId,
                                       @QueryParam(value = "keyword") String keyword) {
        return KafkaSource.findById(sourceId)
                .onItem().transform(source -> {
                    LOGGER.info("get value from source id:{}, value:{}", sourceId, source);
                    KafkaSource kafkaSource = (KafkaSource) source;
                    List<TopicVO> topics = KafkaUtils.queryTopics(kafkaSource.getBroker(), keyword);
                    return new Result(Result.SUCCESS_CODE, null, topics);
                });
    }

    @Path(value = "/kafka/topic/detail/query")
    @GET
    public Uni<Result> queryKafkaTopicDetail(@QueryParam(value = "sourceId") Long sourceId,
                                             @QueryParam(value = "topic") String topic) throws JsonProcessingException {
        return KafkaSource.findById(sourceId).onItem().transform(source -> {
            KafkaSource kafkaSource = (KafkaSource) source;
            TopicDescriptionVO description = KafkaUtils.getTopicDetail(kafkaSource.getBroker(), topic);
            return new Result(Result.SUCCESS_CODE, null, description);
        });
    }

    @Path(value = "/kafka/topic/create")
    @POST
    public Uni<Result> createTopic(TopicRequest request) throws ExecutionException, InterruptedException {
        return KafkaSource.findById(request.getSourceId()).onItem().transform(source -> {
            KafkaSource kafkaSource = (KafkaSource) source;
            try {
                KafkaUtils.createTopic(kafkaSource.getBroker(), request.getTopic(), request.getPartition(), request.getReplica());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return new Result(Result.SUCCESS_CODE, null, null);
        });
    }

    @Path(value = "/kafka/topic/delete")
    @DELETE
    public Uni<Result> createTopic(@QueryParam(value = "sourceId") Long sourceId,
                                   @QueryParam(value = "topic") String topic) {
        return KafkaSource.findById(sourceId).onItem().transform(source -> {
            KafkaSource kafkaSource = (KafkaSource) source;
            KafkaUtils.deleteTopic(kafkaSource.getBroker(), topic);
            return new Result(Result.SUCCESS_CODE, null, null);
        });
    }

    @Path("/kafka/consumer/query")
    @GET
    public Uni<Result> getGroupByTopic(@QueryParam(value = "sourceId") Long sourceId,
                                       @QueryParam(value = "topic") String topic) {
        return KafkaSource.findById(sourceId).onItem().transform(source -> {
            KafkaSource kafkaSource = (KafkaSource) source;
            List<Map<String, Object>> consumers = KafkaUtils.getConsumers(kafkaSource.getBroker(), topic);
            return new Result(Result.SUCCESS_CODE, null, consumers);
        });
    }

    @Path("/kafka/consumer/detail")
    @GET
    public Uni<Result> getGroupDetail(@QueryParam(value = "sourceId") Long sourceId,
                                      @QueryParam(value = "group") String group) {
        return KafkaSource.findById(sourceId).onItem().transform(source -> {
            KafkaSource kafkaSource = (KafkaSource) source;
            List<Map<String, Object>> consumers = KafkaUtils.getConsumerDetail(kafkaSource.getBroker(), group);
            return new Result(Result.SUCCESS_CODE, null, consumers);
        });
    }

    @Path(value = "/kafka/addr")
    @GET
    public Uni<String> getAddr(@Context UriInfo uriInfo) {
        //获取浏览器访问地址中的ip和端口，防止容器运行时候产生问题
        return Uni.createFrom().item(uriInfo.getBaseUri().getHost() + ":" + uriInfo.getBaseUri().getPort());
    }

    @Path("/kafka/message/produce")
    @POST
    public Uni<Result> produceMessage(KafkaMessageRequest request) {
        return KafkaSource.findById(request.getSourceId()).onItem().transform(source -> {
            KafkaSource kafkaSource = (KafkaSource) source;
            KafkaUtils.produceMessage(kafkaSource.getBroker(), request);
            return new Result(Result.SUCCESS_CODE, null, null);
        });
    }

    @Path("/kafka/cluster/info")
    @POST
    public Uni<Result> getClusterInfo(KafkaMessageRequest request) {
        return KafkaSource.findById(request.getSourceId()).onItem().transform(source -> {
            KafkaSource kafkaSource = (KafkaSource) source;
            List<Broker> brokers = KafkaUtils.getClusterInfo(kafkaSource.getBroker());
            return new Result(Result.SUCCESS_CODE, null, brokers);
        });
    }
}
