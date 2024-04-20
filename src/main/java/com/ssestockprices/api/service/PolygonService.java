package com.ssestockprices.api.service;

import com.ssestockprices.api.model.DailyOpenClose;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PolygonService {
    private final WebClient.Builder webClientBuilder;

    public PolygonService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<DailyOpenClose> getOpenClose(String symbol, String date, String apiKey) {
        return webClientBuilder.build()
                .get()
                .uri("https://api.polygon.io/v1/open-close/{symbol}/{date}?adjusted=true&apiKey={apiKey}", symbol, date, apiKey)
                .retrieve()
                .bodyToMono(DailyOpenClose.class);
    }
}
