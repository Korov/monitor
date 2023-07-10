use std::sync::Arc;

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

    let app = routes::demo::create_router(app_state);

    // Run the server on a random address.
    let server = TestServer::new(app.into_make_service()).unwrap();

    // Get the request.
    let response = server.get("/").await;

    response.assert_text("Hello, world!");
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
