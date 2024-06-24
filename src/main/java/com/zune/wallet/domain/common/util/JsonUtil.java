package com.zune.wallet.domain.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Created by Zune on 2020-03-17
 */
@Slf4j
public class JsonUtil {
    private JsonUtil() {
    }

    private static final ObjectMapper defaultMapper;

    static {
        defaultMapper = new ObjectMapper();
        defaultMapper.registerModule(new JavaTimeModule());
        defaultMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static String convertToJson(Object obj) {
        String jsonData;
        try {
            jsonData = defaultMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("json converting fail objectName[{}]", Objects.nonNull(obj) ? obj.getClass().getName() : null, e);
            jsonData = "{}";
        }
        return jsonData;
    }
}
