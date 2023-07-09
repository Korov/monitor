use std::sync::Arc;

use super::{demo::AppState, entity::KafkaSource};
use axum::{extract::State, response::IntoResponse, routing::post, Router};

pub fn create_router(app_state: Arc<AppState>) -> Router {
    Router::new()
        .route("/kafka/add", post(add_kafka_source))
        .with_state(app_state)
}

async fn add_kafka_source(
    source: KafkaSource,
    State(data): State<Arc<AppState>>,
) -> impl IntoResponse<&str, _> {
    // let kafka_source = KafkaSource {
    //     id: 1,
    //     name: String::from("test"),
    //     broker: String::from("localhost:9092"),
    // };
    Ok("Hello, world!")
}
