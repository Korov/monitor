pub mod routes;
use serde::{Deserialize, Serialize};

#[derive(sqlx::FromRow, Debug, Serialize, Deserialize)]
pub struct KafkaSource {
    id: i64,
    name: String,
    broker: String,
}
