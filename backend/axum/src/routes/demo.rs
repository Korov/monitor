use axum::Router;
use axum::routing::get;
use log::info;

use crate::Person;

pub fn create_router() -> Router {
    Router::new().route("/", get(handler))
}

async fn handler() -> &'static str {
    info!("this is a info log");
    let person = Person::new(String::from("Alice"), 25);
    person.say_hello();
    "Hello, world!"
}