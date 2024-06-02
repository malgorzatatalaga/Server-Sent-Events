package com.ssestockprices.streaming.service;

import com.ssestockprices.api.model.CurrentStockInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.stream.Stream;

@Service
public class StockDataService {
    private final Random random = new Random();

    public Flux<CurrentStockInfo> generateDataStream(String symbol, double initialPrice, long initialVolume) {
        return Flux.fromStream(
                        Stream.generate(() -> generateRandomCurrentStockInfo(symbol, initialPrice, initialVolume))
                )
                .delayElements(Duration.ofSeconds(1));
    }

    private CurrentStockInfo generateRandomCurrentStockInfo(String symbol, double lastPrice, long lastVolume) {
        double changePercent = random.nextDouble() * 0.1 - 0.05;
        double currentPrice = Math.max(0, lastPrice * (1 + changePercent));
        double bidPrice = currentPrice * 0.995;
        double askPrice = currentPrice * 1.005;
        long volume = lastVolume + random.nextInt(1000) - 500;

        return new CurrentStockInfo(symbol, currentPrice, volume, bidPrice, askPrice, changePercent * 100);
    }
}