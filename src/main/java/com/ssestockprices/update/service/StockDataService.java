package com.ssestockprices.update.service;

import com.ssestockprices.api.model.CurrentStockInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Service
public class StockDataService {
    private final Random random = new Random();

    public Flux<CurrentStockInfo> generateDataStream(String symbol, double initialPrice, long initialVolume) {
        return Flux.interval(Duration.ofSeconds(1))
                .scan(new CurrentStockInfo(symbol, initialPrice, initialVolume, initialPrice * 0.995, initialPrice * 1.005, 0),
                        (lastInfo, tick) -> generateRandomCurrentStockInfo(symbol, lastInfo.currentPrice(), lastInfo.volume()))
                .skip(1); // Skip the initial state to allow for the first update interval
    }

    private CurrentStockInfo generateRandomCurrentStockInfo(String symbol, double lastPrice, long lastVolume) {
        double changePercent = random.nextDouble() * 0.1 - 0.05; // Generate a random percentage change
        double currentPrice = Math.max(0, lastPrice * (1 + changePercent)); // Ensure price doesn't go negative
        double bidPrice = currentPrice * 0.995;
        double askPrice = currentPrice * 1.005;
        long volume = lastVolume + random.nextInt(1000) - 500; // Randomly adjust the volume

        return new CurrentStockInfo(symbol, currentPrice, volume, bidPrice, askPrice, changePercent * 100);
    }
}