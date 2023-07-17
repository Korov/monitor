use std::sync::Arc;

use axum::{http::StatusCode, Router};
use axum_test_helper::TestClient;

use monitor_lib::routes::{self, entity::AppState};
use sqlx::mysql::MySqlPoolOptions;

async fn get_client() -> TestClient {
    log4rs::init_file("log4rs.yml", Default::default()).unwrap();
    let db_url = "mysql://monitor:monitor@localhost:3309/monitor";
    let pool = MySqlPoolOptions::new()
        .max_connections(5)
        .connect(&db_url)
        .await
        .unwrap();

    let app_state = Arc::new(AppState { db: pool.clone() });

    let kafka_router = routes::kafka_router::create_router(&app_state);
    let app = Router::new().merge(kafka_router);
    TestClient::new(app)
}

#[tokio::test]
async fn test_handler() {
    let client = get_client().await;

    let response = client
        .post("/kafka/add")
        .header("content-type", "application/json")
        .body("{\"name\":\"test1\",\"broker\":\"localhost\"}")
        .send()
        .await;

    assert_eq!(response.status(), StatusCode::OK);
}

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
