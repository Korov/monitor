#[cfg(test)]
mod tests {
    use std::time::Duration;

    use log::info;
    use zookeeper::{Acl, CreateMode, WatchedEvent, Watcher, ZooKeeper};

    struct LoggingWatcher;
    impl Watcher for LoggingWatcher {
        fn handle(&self, e: WatchedEvent) {
            info!("{:?}", e)
        }
    }

    #[test]
    fn list_path() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();
        let zk_urls = "localhost:2183";
        info!("connecting to {}", zk_urls);

        let zk = ZooKeeper::connect(&*zk_urls, Duration::from_secs(15), LoggingWatcher).unwrap();

        zk.add_listener(|zk_state| info!("New ZkState is {:?}", zk_state));

        let auth = zk.add_auth("digest", vec![1, 2, 3, 4]);
        info!("authenticated -> {:?}", auth);

        let path = zk.create(
            "/test",
            vec![1, 2],
            Acl::open_unsafe().clone(),
            CreateMode::Ephemeral,
        );
        info!("created -> {:?}", path);

        let exists = zk.exists("/test", false).unwrap();

        info!("exists -> {:?}", exists);

        let doesnt_exist = zk.exists("/blabla", true);

        info!("don't exists path -> {:?}", doesnt_exist);

        let children = zk.get_children("/", true);
        info!("children of / -> {:?}", children);

        let set_data = zk.set_data("/test", vec![6, 5, 4, 3], None);
        info!("set_data -> {:?}", set_data);
    
        let get_data = zk.get_data("/test", true);
        info!("get_data -> {:?}", get_data);
    }
}
