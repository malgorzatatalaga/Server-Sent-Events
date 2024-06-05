package com.ssestockprices.api.handler;

import com.ssestockprices.api.config.ApiConfig;
import com.ssestockprices.api.model.CurrentStockInfo;
import com.ssestockprices.api.service.PolygonService;
import com.ssestockprices.streaming.service.StockDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PolygonHandler {
    private final PolygonService polygonService;
    private final StockDataService stockDataService;
    private final String apiKey;

    public PolygonHandler(PolygonService polygonService, StockDataService stockDataService, ApiConfig apiConfig) {
        this.polygonService = polygonService;
        this.stockDataService = stockDataService;
        this.apiKey = apiConfig.getPolygonApiKey();
    }

    public Mono<ServerResponse> getOpenCloseAndStream(ServerRequest serverRequest) {
        String symbol = serverRequest.pathVariable("symbol");
        String date = serverRequest.pathVariable("date");

        return polygonService.getOpenClose(symbol, date, apiKey).flatMap(dailyOpenClose -> {
                    double initialPrice = dailyOpenClose.close();
                    long initialVolume = dailyOpenClose.volume();

                    Flux<CurrentStockInfo> dataStream = stockDataService.generateDataStream(symbol, initialPrice, initialVolume)
                            .onBackpressureBuffer(100, BufferOverflowStrategy.DROP_OLDEST);
                    return ServerResponse.ok()
                            .contentType(MediaType.TEXT_EVENT_STREAM)
                            .body(dataStream, CurrentStockInfo.class);
                })
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request", e));
    }

}
