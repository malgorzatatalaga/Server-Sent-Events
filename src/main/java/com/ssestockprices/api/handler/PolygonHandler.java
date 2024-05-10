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

            // Create a message about the stock requested
            String initialMessage = "You requested " + symbol.toUpperCase() + " stock";
            Flux<String> messageFlux = Flux.just(initialMessage);

            // Merge the message with the data stream, converting DailyOpenClose to a string if necessary
            Flux<String> combinedStream = Flux.concat(messageFlux, dataStream.map(this::formatDailyOpenClose));

            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(combinedStream, String.class);
        }).onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue("Error: " + e.getMessage()));
    }

    private String formatDailyOpenClose(DailyOpenClose doc) {
        return "After Hours: " + doc.afterHours() +
                ", Close: " + doc.close() +
                ", High: " + doc.high() +
                ", Low: " + doc.low() +
                ", Open: " + doc.open() +
                ", Volume: " + doc.volume();
    }
}


