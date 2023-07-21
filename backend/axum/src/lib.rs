use std::sync::Arc;

use axum::Router;
use routes::entity::AppState;
use sqlx::mysql::MySqlPoolOptions;

pub mod routes;

pub async fn create_app() -> Router {
    log4rs::init_file("log4rs.yml", Default::default()).unwrap();
    let db_url = "mysql://monitor:monitor@localhost:3309/monitor";
    let pool = MySqlPoolOptions::new()
        .max_connections(5)
        .connect(&db_url)
        .await
        .unwrap();

    let app_state = Arc::new(AppState { db: pool.clone() });

    let kafka_router = routes::kafka_router::create_router(&app_state);
    let ws_router = routes::websocket_server::create_router(&app_state);
    return Router::new().merge(kafka_router).merge(ws_router);
}