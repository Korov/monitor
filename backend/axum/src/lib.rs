use std::{io::{self, BufRead, BufReader}, sync::Arc, env, fs::File};

use axum::Router;
use log::debug;
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
pub mod utils;

// 用来格式化日志的输出时间格式
struct LocalTimer;

impl FormatTime for LocalTimer {
    fn format_time(&self, w: &mut Writer<'_>) -> std::fmt::Result {
        write!(w, "{}", Local::now().format("%FT%T%.3f"))
    }
}

pub async fn init_env() {
    // 从文件中读取环境变量并将其设置为系统变量
    if let Ok(file) = File::open(".env") {
        let reader = BufReader::new(file);
        for line in reader.lines() {
            if let Ok(line) = line {
                let mut parts = line.split('=');
                if let Some(key) = parts.next() {
                    if let Some(value) = parts.next() {
                        env::set_var(key, value);
                        debug!(".env file add key:{}, value:{}", key, value);
                    }
                }
            }
        }
    }
}


pub async fn init_tracing() {
    init_env().await;
    let env_filter = EnvFilter::from_default_env().add_directive(Level::INFO.into());

    let file_appender = tracing_appender::rolling::daily(env::var("log_dir").unwrap(), env::var("log_file_name").unwrap());
    let (non_blocking, _guard) = tracing_appender::non_blocking(file_appender);
    // 初始化并设置日志格式(定制和筛选日志)
    let file_layer: fmt::Layer<tracing_subscriber::layer::Layered<EnvFilter, tracing_subscriber::Registry>, fmt::format::DefaultFields, fmt::format::Format<fmt::format::Full, LocalTimer>, tracing_appender::non_blocking::NonBlocking> = fmt::layer()
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

    let db_url = env::var("mysql_url").unwrap();
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
