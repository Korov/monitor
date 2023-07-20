use futures_util::TryStreamExt;
use std::{collections::HashMap, sync::Arc};

use super::entity::{AppState, KafkaSource, PageVO, Response};
use axum::{
    extract::Query,
    routing::{delete, get, post},
    Json, Router,
};
use log::{error, info};

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
        .route(
            "/kafka/query",
            get({
                let shared_state = Arc::clone(app_state);
                || query_kafka_source(shared_state)
            }),
        )
        .route(
            "/kafka/page/query",
            get({
                let shared_state = Arc::clone(app_state);
                move |body| page_query_kafka_source(body, shared_state)
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
    let id = params.get("id").unwrap().parse::<i64>().unwrap_or(0);
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

async fn query_kafka_source(state: Arc<AppState>) -> Json<Response<Vec<KafkaSource>>> {
    let mut stream =
        sqlx::query_as::<_, KafkaSource>("SELECT * FROM kafka_source").fetch(&state.db);
    let mut sources = Vec::new();
    while let Some(source) = stream.try_next().await.unwrap() {
        sources.push(source);
    }
    info!("query all source size:{}", sources.len());
    Json(Response {
        code: 1,
        message: None,
        data: Some(sources),
    })
}

async fn page_query_kafka_source(
    Query(params): Query<HashMap<String, String>>,
    state: Arc<AppState>,
) -> Json<PageVO<KafkaSource>> {
    info!("param:{:?}", params.get("start_page"));
    info!("param:{:?}", params.contains_key("start_page"));
    let mut start_page: i64 = 1;
    let mut page_size: i64 = 10;
    if params.contains_key("start_page") {
        start_page = match params.get("start_page").unwrap().parse::<i64>() {
            Ok(n) => n,
            Err(_) => {
                error!("invalid start_page:{:?}", params.get("start_page").unwrap());
                1
            }
        };
    }
    if params.contains_key("page_size") {
        page_size = match params.get("page_size").unwrap().parse::<i64>() {
            Ok(n) => n,
            Err(_) => {
                error!("invalid start_page:{:?}", params.get("page_size").unwrap());
                10
            }
        };
    }
    info!("start_page:{}, page_size:{}", start_page, page_size);

    let total_future = sqlx::query_as::<_, (i64,)>("SELECT COUNT(*) as count FROM kafka_source")
        .fetch_one(&state.db);

    let mut stream = sqlx::query_as::<_, KafkaSource>("SELECT * FROM kafka_source LIMIT ?,?")
        .bind(1)
        .bind(10)
        .fetch(&state.db);
    let mut sources = Vec::new();
    while let Some(source) = stream.try_next().await.unwrap() {
        sources.push(source);
    }
    let total = total_future.await.unwrap().0;
    info!("query all source size:{}, total:{}", sources.len(), total);
    Json(PageVO {
        total,
        start_page,
        page_size,
        page_data: None,
    })
}