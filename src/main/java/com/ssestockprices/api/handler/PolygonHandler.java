package com.ssestockprices.api.handler;

import com.ssestockprices.api.config.ApiConfig;
import com.ssestockprices.api.model.DailyOpenClose;
import com.ssestockprices.api.service.PolygonService;
import com.ssestockprices.update.service.StockDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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
        String apiKey = this.apiKey;

        return polygonService.getOpenClose(symbol, date, apiKey).flatMap(doc -> {
            Flux<DailyOpenClose> dataStream = stockDataService.generateDataStream(symbol, doc);
            return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(dataStream, DailyOpenClose.class);
        }).onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error: " + e.getMessage()));
    }
}


