#[cfg(test)]
mod tests {
    use monitor_lib::{init_tracing, utils::kafka_utils::query_topics};
    use tracing::info;

    #[tokio::test]
    async fn query_topics_test() {
        init_tracing().await;
        let topics = query_topics("localhost:9095".to_string(), None);
        for topic in topics {
            info!("name:{}", topic.name);
        }
    }
}
