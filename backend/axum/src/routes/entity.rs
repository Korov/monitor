use std::fmt;

use serde::{Deserialize, Serialize};
use sqlx::{MySql, Pool};

pub struct AppState {
    pub db: Pool<MySql>,
}

#[derive(Debug, serde::Deserialize, serde::Serialize)]
pub struct Response<T: std::fmt::Display> {
    pub code: i64,
    pub message: Option<String>,
    pub data: Option<T>,
}

impl<T> fmt::Display for Response<T>
where
    T: std::fmt::Display + std::fmt::Debug,
{
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(
            f,
            "Response {{ code: {}, message: {:?}, data: {:?} }}",
            self.code, self.message, self.data
        )
    }
}

#[derive(sqlx::FromRow, Debug, Serialize, Deserialize)]
pub struct KafkaSource {
    #[serde(default)]
    pub id: i64,
    pub name: String,
    pub broker: String,
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
