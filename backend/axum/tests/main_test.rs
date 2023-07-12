use std::sync::Arc;

use axum::{http::StatusCode, Router};
use axum_test_helper::TestClient;

use monitor_lib::routes::{self, demo::AppState};
use sqlx::mysql::MySqlPoolOptions;

#[tokio::test]
async fn test_handler() {
    log4rs::init_file("log4rs.yml", Default::default()).unwrap();
    let db_url = "mysql://monitor:monitor@localhost:3309/monitor";
    let pool = MySqlPoolOptions::new()
        .max_connections(5)
        .connect(&db_url)
        .await
        .unwrap();

    let app_state = Arc::new(AppState { db: pool.clone() });

    let demo_router = routes::demo::create_router(&app_state);
    let kafka_router = routes::kafka_router::create_router(&app_state);

    let app = Router::new().merge(demo_router).merge(kafka_router);
    let client = TestClient::new(app);

    let mut get_response = client
        .get("/")
        .header("content-type", "application/json")
        .body("{\"name\":\"test1\",\"broker\":\"localhost\"}")
        .send()
        .await;
    assert_eq!(get_response.status(), StatusCode::OK);
    let body = get_response.chunk_text().await.unwrap();
    assert_eq!(&body, "Hello, world!");

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
    #[test]
    #[should_panic(expected = "Guess value must be less than or equal to 100")]
    fn print() {
        log4rs::init_file("log4rs.yml", Default::default()).unwrap();
        print!("test print")
    }
}
