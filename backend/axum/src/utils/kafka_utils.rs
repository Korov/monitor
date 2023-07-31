use std::time::Duration;

use rdkafka::{
    consumer::{BaseConsumer, Consumer},
    ClientConfig,
};

use crate::routes::entity::TopicVO;

pub fn query_topics(broker: String, keyword: Option<String>) -> Vec<TopicVO> {
    let consumer: BaseConsumer = ClientConfig::new()
        .set("bootstrap.servers", broker)
        .create()
        .expect("Consumer creation failed");

    let metadata = consumer
        .fetch_metadata(None, Duration::from_millis(10000))
        .expect("Failed to fetch metadata");

    let mut topics: Vec<TopicVO> = Vec::new();
    for topic in metadata.topics() {
        let topic_name = topic.name();
        match keyword {
            Some(ref n) => {
                if topic_name.contains(n) {
                    let topic_vo = TopicVO {
                        name: topic_name.to_owned(),
                        is_internal: false,
                    };
                    topics.push(topic_vo);
                }
            }
            None => {
                let topic_vo = TopicVO {
                    name: topic_name.to_owned(),
                    is_internal: false,
                };
                topics.push(topic_vo);
            }
        }
    }
    return topics;
}
