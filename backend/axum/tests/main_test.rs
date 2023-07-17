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

    #[test]
    fn print() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();
        info!("aaa");
        print!("test print")
    }
}
