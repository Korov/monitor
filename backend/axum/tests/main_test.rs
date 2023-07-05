use axum_test::TestServer;

use monitor_lib::routes;

#[tokio::test]
async fn test_handler() {
    log4rs::init_file("log4rs.yml", Default::default()).unwrap();

    let router = routes::demo::create_router();

    // Run the server on a random address.
    let server = TestServer::new(router.into_make_service()).unwrap();

    // Get the request.
    let response = server.get("/").await;

    response.assert_text("Hello, world!");
}
