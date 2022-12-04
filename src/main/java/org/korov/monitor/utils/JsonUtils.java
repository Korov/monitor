package org.korov.monitor.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author korov
 */
public class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    static {
        DEFAULT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    public static final ObjectMapper SNAKE_CASE_MAPPER = new ObjectMapper();

    static {
        SNAKE_CASE_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        SNAKE_CASE_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String objectToJson(Object object) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.error("parse object to json failed");
        }
        return null;
    }

    public static <T> T jsonToObject(String json, Class<T> valueType) {
        try {
            return jsonToObject(json, valueType, DEFAULT_MAPPER);
        } catch (JsonProcessingException e) {
            LOGGER.error("parse json {} to object [{}] failed, {}", json, valueType.getName(), e.getMessage());
        }
        return null;
    }

    public static <T> T jsonToObject(String json, Class<T> valueType, ObjectMapper mapper) throws JsonProcessingException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(valueType);
        Objects.requireNonNull(mapper);
        return mapper.readValue(json, valueType);
    }
}
