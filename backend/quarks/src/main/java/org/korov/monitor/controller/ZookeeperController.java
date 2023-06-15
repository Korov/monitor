package org.korov.monitor.controller;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.korov.monitor.entity.ZookeeperSource;
import org.korov.monitor.utils.ZookeeperUtils;
import org.korov.monitor.vo.Result;
import org.korov.monitor.vo.ZNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author korov
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ZookeeperController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperController.class);

    @Path("/zookeeper/address/add")
    @POST
    public Uni<Result> addKafkaSource(ZookeeperSource zookeeperSource) {
        return Panache.withTransaction(zookeeperSource::persist).replaceWith(zookeeperSource).onItem()
                .transform(source -> new Result(Result.SUCCESS_CODE, null, source));
    }

    @Path("/zookeeper/address/del")
    @DELETE
    public Uni<Result> addKafkaSource(@QueryParam("id") Long id) {
        return Panache.withTransaction(() -> ZookeeperSource.deleteById(id)).onItem()
                .transform((deleted) -> new Result(Result.SUCCESS_CODE, null, deleted));
    }

    @Path(value = "/zookeeper/address/query")
    @GET
    public Uni<Result> queryKafkaSource() {
        return ZookeeperSource.listAll().onItem().transform(list -> new Result(Result.SUCCESS_CODE, null, list));
    }

    @Path(value = "/zookeeper/tree")
    @GET
    public Uni<Result> queryKafkaSource(@QueryParam("host") String host, @QueryParam(value = "path") String path,
                                        @QueryParam(value = "recursion") Boolean recursion) {
        return Uni.createFrom().item(host).onItem()
                .transform(zkHost -> new Result(Result.SUCCESS_CODE, null, ZookeeperUtils.getAllZnode(zkHost, path, recursion)));
    }
}
