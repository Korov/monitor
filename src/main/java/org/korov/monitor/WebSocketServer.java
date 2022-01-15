package org.korov.monitor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.korov.monitor.entity.KafkaSource;
import org.korov.monitor.utils.JsonUtils;
import org.korov.monitor.utils.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ServerEndpoint("/push/websocket")
@ApplicationScoped
public class WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static final CopyOnWriteArraySet<WebSocketServer> WEB_SOCKET_SERVERS = new CopyOnWriteArraySet<WebSocketServer>();
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(16, 1024,
            60000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("kafka-consumer-%d").build());
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    Map<String, String> params = new HashMap<>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //接收sid
    private String sid = "";

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    /**
     * 实现服务器主动推送
     *
     * @param message
     */
    public void sendMessage(String message) throws IOException, EncodeException {
        /*this.session.getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                log.error("Unable to send message, ", result.getException());
            }
        });*/
        this.session.getBasicRemote().sendObject(message);
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) throws Exception {
        this.session = session;
        WEB_SOCKET_SERVERS.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        log.info("有新窗口开始监听:" + sid + ",当前在线人数为" + getOnlineCount());
        this.sid = sid;

        String queryString = session.getQueryString();
        log.info(queryString);
        String[] array = queryString.split("&");
        for (String p : array) {
            String[] split = p.split("=");
            params.put(split[0], split[1]);
        }
        KafkaSource kafkaSource = (KafkaSource) KafkaSource.findById(Long.parseLong(params.get("sourceId"))).subscribeAsCompletionStage().get();
        consume(this.session, kafkaSource.getBroker(), params.get("topic"), params.get("group"), params.get("reset"), params.get("partition"), params.get("offset"));
    }

    public void consume(Session session, String broker, String topic, String group, String reset, String partition, String offset) {
        THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR.execute(new Thread(() -> {
            KafkaConsumer<String, String> consumer;
            List<TopicPartition> topicPartitions = new ArrayList<>();
            if (partition != null && offset != null && !"null".equals(partition) && !"null".equals(offset)) {
                topicPartitions.add(new TopicPartition(topic, Integer.parseInt(partition)));
                consumer = KafkaUtils.getConsumer(broker, group, reset);
                consumer.assign(topicPartitions);
                long defaultOffset = 0L;
                try {
                    defaultOffset = Long.parseLong(offset);
                } catch (Exception e) {
                    log.error("invalid offset:{}", offset);
                }
                for (TopicPartition topicPartition : topicPartitions) {
                    consumer.seek(topicPartition, defaultOffset);
                }
            } else {
                consumer = KafkaUtils.getConsumer(broker, group, reset);
                topicPartitions = KafkaUtils.getTopicPartitions(broker, topic);
                consumer.assign(topicPartitions);
            }


            while (session.isOpen()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, String> text = new LinkedHashMap<>();
                    text.put("topic", record.topic());
                    text.put("partition", String.valueOf(record.partition()));
                    text.put("offset", String.valueOf(record.offset()));
                    text.put("key", record.key());
                    text.put("value", record.value());
                    try {
                        /*this.session.getAsyncRemote().sendText(JsonUtils.jsonString(text), result -> {
                            if (result.getException() != null) {
                                log.error("Unable to send message, ", result.getException());
                            }
                        });*/
                        this.session.getBasicRemote().sendText(JsonUtils.jsonString(text));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.info("kafka consumer closed");
        }));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        WEB_SOCKET_SERVERS.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        //log.info("收到来自窗口"+sid+"的信息:"+message);
        if ("heart".equals(message)) {
            try {
                sendMessage("heartOk");
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketServer that = (WebSocketServer) o;
        return Objects.equals(params, that.params) && Objects.equals(session, that.session) && Objects.equals(sid, that.sid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params, session, sid);
    }
}
