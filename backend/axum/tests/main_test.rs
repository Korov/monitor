#[cfg(test)]
mod tests {
    use log::info;
    use tracing::{event, instrument, span, Level};

    #[test]
    fn print() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();
        info!("aaa");

        // records an event outside of any span context:
        event!(Level::INFO, "something happened");

        let span = span!(Level::INFO, "my_span");
        let _guard = span.enter();

        // records an event within "my_span".
        event!(Level::WARN, "something happened inside my_span");

        print!("test print")
    }

    #[test]
    fn test_instrument() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();
        add(32, 45);
    }

    #[instrument]
    fn add(value1: i32, value2: i32) -> i32 {
        event!(Level::INFO, "start add");
        return value1 + value2;
    }
}
