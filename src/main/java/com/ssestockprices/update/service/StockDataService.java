package com.ssestockprices.update.service;

import com.ssestockprices.api.model.DailyOpenClose;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@Service
public class StockDataService {
    private final Random random = new Random();

    public Flux<DailyOpenClose> generateDataStream(String symbol, DailyOpenClose initialData) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> generateRandomDailyOpenClose(symbol, initialData));
    }

    private DailyOpenClose generateRandomDailyOpenClose(String symbol, DailyOpenClose lastData) {
        double changeFactor = 0.05; // maksymalnie 5% zmiana
        return new DailyOpenClose(
                adjustPrice(lastData.afterHours(), changeFactor),
                adjustPrice(lastData.close(), changeFactor),
                "From " + Instant.now(),
                adjustPrice(lastData.high(), changeFactor),
                adjustPrice(lastData.low(), changeFactor),
                adjustPrice(lastData.open(), changeFactor),
                adjustPrice(lastData.preMarket(), changeFactor),
                "OK",
                symbol,
                lastData.volume() + random.nextInt(500) - 250
        );
    }

    private double adjustPrice(double basePrice, double changeFactor) {
        double changeAmount = basePrice * (random.nextDouble() * changeFactor * 2 - changeFactor);
        return Math.max(0, basePrice + changeAmount);
    }
}
