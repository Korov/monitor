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
        DEFAULT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    public static String jsonString(Object object) throws JsonProcessingException {
        return DEFAULT_MAPPER.writeValueAsString(object);
    }
}
