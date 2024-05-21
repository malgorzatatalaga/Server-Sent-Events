package com.ssestockprices.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssestockprices.api.model.DailyOpenClose;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PolygonUtils {
    private final ObjectMapper objectMapper;

    public PolygonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DailyOpenClose deserializeJsonToDailyOpenClose(String json) throws IOException {
        return objectMapper.readValue(json, DailyOpenClose.class);
    }
}
