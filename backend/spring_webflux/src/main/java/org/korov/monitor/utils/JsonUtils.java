package org.korov.monitor.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author korov
 */
@Data
@Slf4j
public class JsonUtils {
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    static {
        DEFAULT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String jsonString(Object object) throws JsonProcessingException {
        return DEFAULT_MAPPER.writeValueAsString(object);
    }

    public static String jsonPretty(Object object) throws JsonProcessingException {
        return DEFAULT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    public static String jsonPretty(String json) throws JsonProcessingException {
        Object jsonObject = DEFAULT_MAPPER.readValue(json, Object.class);
        return DEFAULT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }
}
