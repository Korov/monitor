#[cfg(test)]
mod tests {

    use axum::http::StatusCode;
    use axum_test_helper::TestClient;

    use log::info;
    use monitor_lib::create_app;

    #[tokio::test]
    async fn add_kafka_source() {
        let app = create_app().await;

        let client = TestClient::new(app);

        let response = client
            .post("/kafka/add")
            .header("content-type", "application/json")
            .body("{\"name\":\"test1\",\"broker\":\"localhost\"}")
            .send()
            .await;

        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn delte_kafka_source() {
        let app = create_app().await;

        let client = TestClient::new(app);

        let response = client
            .delete("/kafka/delete?id=12")
            .header("content-type", "application/json")
            .send()
            .await;

        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn query_kafka_source() {
        let app = create_app().await;

        let client = TestClient::new(app);

        let response = client
            .get("/kafka/query")
            .header("content-type", "application/json")
            .send()
            .await;

    
        assert_eq!(response.status(), StatusCode::OK);

        let text = response.bytes().await;
        info!("response:{:?}", text);
    }
}
