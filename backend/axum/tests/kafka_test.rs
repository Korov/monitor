#[cfg(test)]
mod tests {
    use kafka::client::KafkaClient;
    use log::info;

    #[test]
    fn print() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();

        let mut client = KafkaClient::new(vec!["192.168.50.8:9095".to_owned()]);
        client.load_metadata_all().unwrap();
        for topic in client.topics().names() {
            info!("topic: {}", topic);
        }
    }
}
