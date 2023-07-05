use axum::{routing::get, Router};
use log::{debug, error, info};
use log4rs;
use monitor_lib::Person;
use std::net::SocketAddr;

#[tokio::main]
async fn main() {
    log4rs::init_file("log4rs.yml", Default::default()).unwrap();

    debug!("this is a debug {}", "message");
    error!("this is printed by default");
    info!("this is a info log");

    let person = Person::new(String::from("Alice"), 25);
    person.say_hello();

    // Route all requests on "/" endpoint to anonymous handler.
    //
    // A handler is an async function which returns something that implements
    // `axum::response::IntoResponse`.

    // A closure or a function can be used as handler.

    let app = Router::new().route("/", get(handler));
    //        Router::new().route("/", get(|| async { "Hello, world!" }));

    // Address that server will bind to.
    let addr = SocketAddr::from(([127, 0, 0, 1], 3000));

    // Use `hyper::server::Server` which is re-exported through `axum::Server` to serve the app.
    axum::Server::bind(&addr)
        // Hyper server takes a make service.
        .serve(app.into_make_service())
        .await
        .unwrap();
}

async fn handler() -> &'static str {
    info!("this is a info log");
    let person = Person::new(String::from("Alice"), 25);
    person.say_hello();
    "Hello, world!"
}
