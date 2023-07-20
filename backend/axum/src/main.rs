use std::net::SocketAddr;
use std::sync::Arc;

use axum::Router;
use sqlx::mysql::MySqlPoolOptions;

use monitor_lib::routes;
use monitor_lib::routes::entity::AppState;

#[tokio::main]
async fn main() {
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

    let app = Router::new().merge(kafka_router).merge(ws_router);


    let addr = SocketAddr::from(([127, 0, 0, 1], 3000));
    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .await
        .unwrap();
}
