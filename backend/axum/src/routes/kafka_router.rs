use std::{collections::HashMap, sync::Arc};

use super::entity::{AppState, KafkaSource, Response};
use axum::{
    extract::Query,
    routing::{delete, post},
    Json, Router,
};
use log::info;

pub fn create_router(app_state: &Arc<AppState>) -> Router {
    Router::new()
        .route(
            "/kafka/add",
            post({
                let shared_state = Arc::clone(app_state);
                move |body| add_kafka_source(body, shared_state)
            }),
        )
        .route(
            "/kafka/delete",
            delete({
                let shared_state = Arc::clone(app_state);
                move |body| delte_kafka_source(body, shared_state)
            }),
        )
}

async fn add_kafka_source(
    Json(source): Json<KafkaSource>,
    state: Arc<AppState>,
) -> Json<Response<KafkaSource>> {
    let rows_affected = sqlx::query("INSERT INTO kafka_source(`name`, `broker`) VALUES (?, ?)")
        .bind(source.name.clone())
        .bind(source.broker.clone())
        .execute(&state.db)
        .await
        .unwrap()
        .rows_affected();
    info!("insert rows:{}, kafka source:{}", rows_affected, source);
    let response = Response {
        code: 1,
        message: None,
        data: None,
    };
    Json(response)
}

async fn delte_kafka_source(
    Query(params): Query<HashMap<String, String>>,
    state: Arc<AppState>,
) -> Json<Response<KafkaSource>> {
    let id = params.get("id").unwrap().parse::<i32>().unwrap_or(0);
    if id <= 0 {
        return Json(Response {
            code: 0,
            message: Some(format!("invalid id:{}", id)),
            data: None,
        });
    }
    let rows_affected = sqlx::query("DELETE FROM kafka_source WHERE id = ?")
        .bind(id)
        .execute(&state.db)
        .await
        .unwrap()
        .rows_affected();
    info!("delete rows:{} with id:{:?}", rows_affected, id);
    Json(Response {
        code: 1,
        message: None,
        data: None,
    })
}
