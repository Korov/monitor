use axum::response::IntoResponse;
use futures_util::TryStreamExt;
use sqlx::mysql::MySqlRow;
use std::sync::Arc;

use axum::Router;
use axum::{extract::State, routing::get};
use log::info;
use sqlx::{MySql, Pool, Row};

use super::entity::KafkaSource;


pub struct AppState {
    pub db: Pool<MySql>,
}

pub fn create_router(app_state: &Arc<AppState>) -> Router {
    let shared_state = Arc::clone(app_state);
    Router::new().route("/", get(handler)).with_state(shared_state)
}

async fn handler(State(state): State<Arc<AppState>>) -> impl IntoResponse {
    let mut sources = sqlx::query("select * from kafka_source limit ?")
        .bind(150_i64)
        .map(|row: MySqlRow| KafkaSource {
            id: row.try_get("id").unwrap(),
            name: row.try_get("name").unwrap(),
            broker: row.try_get("broker").unwrap(),
        })
        .fetch(&state.db);

    while let Some(source) = sources.try_next().await.unwrap() {
        info!(
            "cast map for source:{}",
            serde_json::to_string(&source).unwrap()
        )
    }

    let mut stream = sqlx::query_as::<_, KafkaSource>("select * from kafka_source limit ?")
        .bind(150_i64)
        .fetch(&state.db);

    while let Some(source) = stream.try_next().await.unwrap() {
        info!(
            "stream map for name:{}",
            serde_json::to_string(&source).unwrap()
        );
    }
    "Hello, world!".into_response()
}
