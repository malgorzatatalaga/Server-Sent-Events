package com.ssestockprices.api.model;

public record CurrentStockInfo(
        String symbol,
        double currentPrice,
        long volume,
        double bidPrice,
        double askPrice,
        double changePercent
) {
}
