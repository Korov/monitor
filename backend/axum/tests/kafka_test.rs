#[cfg(test)]
mod tests {
    use std::time::Duration;

    use log::{info, warn};
    use rdkafka::{
        admin::{AdminClient, AdminOptions, NewTopic, TopicReplication},
        config::{FromClientConfig, RDKafkaLogLevel},
        consumer::{BaseConsumer, CommitMode, Consumer, ConsumerContext, Rebalance},
        error::KafkaResult,
        message::{Headers, OwnedHeaders},
        producer::{FutureProducer, FutureRecord},
        ClientConfig, ClientContext, Message, TopicPartitionList,
    };
    use tokio::runtime::Runtime;

    #[test]
    fn create_topic() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();

        // create Kafka configuration object
        let mut config = ClientConfig::new();

        // set broker address
        config.set("bootstrap.servers", "localhost:9095");

        let admin_options = AdminOptions::new();

        // create AdminClient instance
        let admin_client = AdminClient::from_config(&config).unwrap();

        let new_topic = NewTopic::new("my-topic", 1, TopicReplication::Fixed(1));

        // 创建一个新的 Tokio 运行时
        let rt = Runtime::new().unwrap();
        // 将异步代码封装成一个 future，并提交到 Tokio 运行时中执行
        let future = async {
            admin_client
                .create_topics(vec![&new_topic], &admin_options)
                .await
                .unwrap()
        };
        let result = rt.block_on(future);
        info!("result:{:?}", result);
    }

    #[test]
    fn produce_message() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();

        let producer: &FutureProducer = &ClientConfig::new()
            .set("bootstrap.servers", "localhost:9095")
            .set("message.timeout.ms", "5000")
            .create()
            .expect("Producer creation error");

        // This loop is non blocking: all messages will be sent one after the other, without waiting
        // for the results.
        let futures = (0..5)
            .map(|i| async move {
                // The send operation on the topic returns a future, which will be
                // completed once the result or failure from Kafka is received.
                let delivery_status =
                    producer
                        .send(
                            FutureRecord::to("my-topic")
                                .payload(&format!("Message {}", i))
                                .key(&format!("Key {}", i))
                                .headers(OwnedHeaders::new().add(
                                    &format!("header_key_{}", i),
                                    &format!("header_value_{}", i),
                                )),
                            Duration::from_secs(0),
                        )
                        .await;

                // This will be executed when the result is received.
                info!("Delivery status for message {} received", i);
                delivery_status
            })
            .collect::<Vec<_>>();

        // 创建一个新的 Tokio 运行时
        let rt = Runtime::new().unwrap();
        // 将异步代码封装成一个 future，并提交到 Tokio 运行时中执行
        let future = async {
            // This loop will wait until all delivery statuses have been received.
            for future in futures {
                info!("Future completed. Result: {:?}", future.await);
            }
        };
        let result = rt.block_on(future);
        info!("result:{:?}", result);
    }

    // A context can be used to change the behavior of producers and consumers by adding callbacks
    // that will be executed by librdkafka.
    // This particular context sets up custom callbacks to log rebalancing events.
    struct CustomContext;

    impl ClientContext for CustomContext {}

    impl ConsumerContext for CustomContext {
        fn pre_rebalance(&self, rebalance: &Rebalance) {
            info!("Pre rebalance {:?}", rebalance);
        }

        fn post_rebalance(&self, rebalance: &Rebalance) {
            info!("Post rebalance {:?}", rebalance);
        }

        fn commit_callback(&self, result: KafkaResult<()>, _offsets: &TopicPartitionList) {
            info!("Committing offsets: {:?}", result);
        }
    }
    // A type alias with your custom consumer can be created for convenience.
    type LoggingConsumer = rdkafka::consumer::StreamConsumer<CustomContext>;
    #[tokio::test]
    async fn consume_message() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();

        let context = CustomContext;
        let consumer: LoggingConsumer = ClientConfig::new()
            .set("group.id", "test123")
            .set("bootstrap.servers", "localhost:9095")
            .set("enable.partition.eof", "false")
            .set("session.timeout.ms", "6000")
            .set("enable.auto.commit", "true")
            //.set("statistics.interval.ms", "30000")
            //.set("auto.offset.reset", "smallest")
            .set_log_level(RDKafkaLogLevel::Debug)
            .create_with_context(context)
            .expect("Consumer creation failed");

        let mut topics = Vec::new();
        topics.push("my-topic");
        consumer
            .subscribe(&topics)
            .expect("Can't subscribe to specified topics");

        loop {
            match consumer.recv().await {
                Err(e) => warn!("Kafka error: {}", e),
                Ok(m) => {
                    let payload = match m.payload_view::<str>() {
                        None => "",
                        Some(Ok(s)) => s,
                        Some(Err(e)) => {
                            warn!("Error while deserializing message payload: {:?}", e);
                            ""
                        }
                    };
                    info!("key: '{:?}', payload: '{}', topic: {}, partition: {}, offset: {}, timestamp: {:?}",
                                  m.key(), payload, m.topic(), m.partition(), m.offset(), m.timestamp());

                    let headers = m.headers().unwrap();
                    for index in 0..headers.count() {
                        let header = headers.get(index).unwrap();
                        info!(
                            "  Header {:#?}: {:?}",
                            header.0,
                            String::from_utf8(header.1.to_vec()).unwrap()
                        );
                    }
                    consumer.commit_message(&m, CommitMode::Async).unwrap();
                }
            };
        }
    }

    #[test]
    fn print_metadata_test() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();

        let consumer: BaseConsumer = ClientConfig::new()
            .set("bootstrap.servers", "localhost:9095")
            .create()
            .expect("Consumer creation failed");

        let metadata = consumer
            .fetch_metadata(None, Duration::from_millis(10000))
            .expect("Failed to fetch metadata");

        for broker in metadata.brokers() {
            info!(
                "  Id: {}  Host: {}:{}  ",
                broker.id(),
                broker.host(),
                broker.port()
            );
        }

        let mut message_count = 0;
        for topic in metadata.topics() {
            info!("  Topic: {}  Err: {:?}", topic.name(), topic.error());
            for partition in topic.partitions() {
                info!(
                    "     Partition: {}  Leader: {}  Replicas: {:?}  ISR: {:?}  Err: {:?}",
                    partition.id(),
                    partition.leader(),
                    partition.replicas(),
                    partition.isr(),
                    partition.error()
                );
                let (low, high) = consumer
                    .fetch_watermarks(topic.name(), partition.id(), Duration::from_secs(1))
                    .unwrap_or((-1, -1));
                info!(
                    "       Low watermark: {}  High watermark: {} (difference: {})",
                    low,
                    high,
                    high - low
                );
                message_count += high - low;
            }
            info!("     Total message count: {}", message_count);
        }
    }
}
