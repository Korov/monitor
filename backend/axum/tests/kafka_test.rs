#[cfg(test)]
mod tests {
    use log::info;

    #[test]
    fn print() {
        info!("test");
        let brokers = vec!["localhost:9092"];
        let client_config = kafka::client::KafkaClientConfig::new(&brokers);
        // Create a Kafka client
        let kafka_client = KafkaClient::new(client_config).unwrap();
    }
}
