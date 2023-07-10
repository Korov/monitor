use std::sync::Arc;

use log::info;
use axum::{Router, http::HeaderName};
use axum_test::TestServer;

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

    // Run the server on a random address.
    let server = TestServer::new(app.into_make_service()).unwrap();

    // Get the request.
    // let response = server.get("/").await;

    // response.assert_text("Hello, world!");
    
    let response = server
        .post("/kafka/add")
        .json("{\"name\":\"test1\",\"broker\":\"localhost\"}")
        .await;
    let response_body = response.bytes();
    let response_body_str = std::str::from_utf8(&response_body).unwrap();
    info!("kafka add response:{}", response_body_str);
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
