package org.korov.monitor.controller.request;

import org.korov.monitor.utils.JsonUtils;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class ConsumerRequestDecoder implements Decoder.Text<ConsumerRequest> {
    @Override
    public ConsumerRequest decode(String s) throws DecodeException {
        return JsonUtils.jsonToObject(s, ConsumerRequest.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
