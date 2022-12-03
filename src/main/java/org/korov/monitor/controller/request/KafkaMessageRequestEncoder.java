package org.korov.monitor.controller.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.korov.monitor.utils.JsonUtils;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class KafkaMessageRequestEncoder implements Encoder.Text<KafkaMessageRequest> {
    @Override
    public String encode(KafkaMessageRequest request) throws EncodeException {
        try {
            return JsonUtils.jsonString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
