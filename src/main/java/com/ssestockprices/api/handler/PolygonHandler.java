package com.ssestockprices.api.handler;

import com.ssestockprices.api.model.DailyOpenClose;
import com.ssestockprices.api.service.PolygonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PolygonHandler {
    private final PolygonService polygonService;

    public PolygonHandler(PolygonService polygonService) {
        this.polygonService = polygonService;
    }

    public Mono<ServerResponse> getOpenClose(ServerRequest serverRequest) {
        String symbol = serverRequest.pathVariable("symbol");
        String date = serverRequest.pathVariable("date");
        String apiKey = "1SlkpIVsIp286yfKp4gE2tN9P0mYHN2w";

        return polygonService.getOpenClose(symbol, date, apiKey).flatMap(doc -> {
            String readableFormat = formatDailyOpenClose(doc);
            return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue(readableFormat);
        }).onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error: " + e.getMessage()));
    }


    private String formatDailyOpenClose(DailyOpenClose doc) {
        return String.format("Date: %s\nSymbol: %s\nOpen: %.2f\nHigh: %.2f\nLow: %.2f\nClose: %.2f\nAfter Hours: %.2f\nPre-Market: %.2f\nVolume: %d", doc.from(), doc.symbol(), doc.open(), doc.high(), doc.low(), doc.close(), doc.afterHours(), doc.preMarket(), doc.volume());
    }
}

