package com.ssestockprices.api.model;

public record DailyOpenClose(
        double afterHours,
        double close,
        String from,
        double high,
        double low,
        double open,
        double preMarket,
        String status,
        String symbol,
        int volume
) {
}
