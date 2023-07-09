use std::fmt;

use axum::{body, response::IntoResponse};
use serde::{Deserialize, Serialize};

pub struct Response<T: std::fmt::Display> {
    pub code: i64,
    pub message: String,
    pub data: T,
}

impl<T: std::fmt::Display> fmt::Display for Response<T> {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "({}, {}, {})", self.code, self.message, self.data)
    }
}

impl<T: std::fmt::Display> IntoResponse for Response<T> {
    type Body = String;
    type BodyError = std::io::Error;

    fn into_response(self) -> axum::http::Response<Self::Body> {
        Response::new(body::boxed(self))
    }
}

#[derive(sqlx::FromRow, Debug, Serialize, Deserialize)]
pub struct KafkaSource {
    id: i64,
    name: String,
    broker: String,
}

impl fmt::Display for KafkaSource {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(
            f,
            "KafkaSource(id:{}, name:{}, broker:{})",
            self.id, self.name, self.broker
        )
    }
}
