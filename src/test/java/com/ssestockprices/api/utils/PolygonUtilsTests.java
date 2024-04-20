package com.ssestockprices.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssestockprices.api.model.DailyOpenClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PolygonUtilsTests {

    @Autowired
    private ObjectMapper objectMapper;

    private PolygonUtils polygonUtils;

    @BeforeEach
    void setUp() {
        polygonUtils = new PolygonUtils(objectMapper);
    }

    @Test
    void testDeserializeJsonToDailyOpenClose_success() throws Exception {
        String json = """
        {
            "afterHours": 185.11,
            "close": 185.85,
            "from": "2024-02-02",
            "high": 187.33,
            "low": 179.25,
            "open": 179.86,
            "preMarket": 182.4,
            "status": "OK",
            "symbol": "AAPL",
            "volume": 102527680
        }
        """;

        DailyOpenClose expected = new DailyOpenClose(185.11, 185.85, "2024-02-02", 187.33, 179.25, 179.86, 182.4, "OK", "AAPL", 102527680);
        DailyOpenClose result = polygonUtils.deserializeJsonToDailyOpenClose(json);

        assertEquals(expected, result);
    }


}
