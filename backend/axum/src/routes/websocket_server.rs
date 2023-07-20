use std::sync::Arc;

use axum::{
    extract::ws::{Message, WebSocket, WebSocketUpgrade},
    response::Response,
    routing::get,
    Router,
};
use log::info;

use super::entity::AppState;

pub fn create_router(app_state: &Arc<AppState>) -> Router {
    Router::new().route(
        "/ws",
        get({
            let shared_state = Arc::clone(app_state);
            move |ws| handler(ws, shared_state)
        }),
    )
}

async fn handler(ws: WebSocketUpgrade, state: Arc<AppState>) -> Response {
    ws.on_upgrade(|socket| handle_socket(socket, state))
}

async fn handle_socket(mut socket: WebSocket, state: Arc<AppState>) {
    info!("state:{:?}", state.db);
    while let Some(msg) = socket.recv().await {
        let msg = if let Ok(msg) = msg {
            msg
        } else {
            // client disconnected
            return;
        };

        let text = msg.clone().into_text().unwrap() + "ffefe";
        let new_msg = Message::Text(text);
        if socket.send(new_msg).await.is_err() {
            // client disconnected
            return;
        }
    }
}
