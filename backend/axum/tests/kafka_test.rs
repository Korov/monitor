#[cfg(test)]
mod tests {
    use kafka::client::KafkaClient;
    use log::info;

    #[test]
    fn print() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();
        info!("test");
        let mut brokers = Vec::new();
        brokers.push("localhost:9092".to_string());
        // Create a Kafka client
        let mut kafka_client = KafkaClient::new(brokers);
        let all_metadata = kafka_client.load_metadata_all();
        info!("metadata:{:?}", all_metadata.unwrap());
    }
}
