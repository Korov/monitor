package org.korov.monitor.controller.request;

import org.korov.monitor.utils.JsonUtils;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class KafkaMessageRequestEncoder implements Encoder.Text<KafkaMessageRequest> {
    @Override
    public String encode(KafkaMessageRequest request) {
        return JsonUtils.objectToJson(request);
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
