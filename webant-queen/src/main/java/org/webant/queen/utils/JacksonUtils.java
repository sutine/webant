package org.webant.queen.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonUtils {
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
    public static String toJson(Object o) {
        String json = "";
        if (o == null) return json;
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }
}
