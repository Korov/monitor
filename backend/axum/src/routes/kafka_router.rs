use std::sync::Arc;

use super::{demo::AppState, entity::KafkaSource};
use axum::{routing::post, Json, Router};
use log::info;

pub fn create_router(app_state: &Arc<AppState>) -> Router {
    Router::new().route(
        "/kafka/add",
        post({
            let shared_state = Arc::clone(app_state);
            move |body| add_kafka_source(body, shared_state)
        }),
    )
}

async fn add_kafka_source(
    Json(source): Json<KafkaSource>,
    state: Arc<AppState>,
) -> Json<KafkaSource> {
    let rows_affected = sqlx::query("INSERT INTO kafka_source(`name`, `broker`) VALUES (?, ?)")
        .bind(source.name.clone())
        .bind(source.broker.clone())
        .execute(&state.db)
        .await
        .unwrap()
        .rows_affected();
    info!("insert rows:{}", rows_affected);
    Json(source)
}
