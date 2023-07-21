use std::{io, sync::Arc};

use axum::Router;
use routes::entity::AppState;
use sqlx::mysql::MySqlPoolOptions;

use chrono::Local;
use tracing::Level;
use tracing_subscriber::{
    filter::EnvFilter,
    fmt::{self, format::Writer, time::FormatTime},
    prelude::__tracing_subscriber_SubscriberExt,
    util::SubscriberInitExt,
};

pub mod routes;

// 用来格式化日志的输出时间格式
struct LocalTimer;

impl FormatTime for LocalTimer {
    fn format_time(&self, w: &mut Writer<'_>) -> std::fmt::Result {
        write!(w, "{}", Local::now().format("%FT%T%.3f"))
    }
}

pub async fn init_tracing() {
    let env_filter = EnvFilter::from_default_env().add_directive(Level::INFO.into());

    let file_appender = tracing_appender::rolling::daily("logs", "monitor.log");
    let (non_blocking, _guard) = tracing_appender::non_blocking(file_appender);
    // 初始化并设置日志格式(定制和筛选日志)
    let file_layer = fmt::layer()
        .event_format(fmt::format().with_timer(LocalTimer))
        .with_writer(io::stdout)
        .with_writer(non_blocking)
        .with_ansi(false)
        .with_line_number(true)
        .with_thread_names(true);

    let console_layer = fmt::layer()
        .event_format(fmt::format().with_timer(LocalTimer))
        .with_writer(io::stdout)
        .with_ansi(true)
        .with_line_number(true)
        .with_thread_names(true);

    tracing_subscriber::registry()
        .with(env_filter)
        .with(file_layer)
        .with(console_layer)
        .init();
}

pub async fn create_app() -> Router {
    init_tracing().await;

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
