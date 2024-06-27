package com.ssestockprices.api.handler;

import com.ssestockprices.api.config.ApiConfig;
import com.ssestockprices.api.exception.InvalidDateException;
import com.ssestockprices.api.model.CurrentStockInfo;
import com.ssestockprices.api.service.PolygonService;
import com.ssestockprices.streaming.service.StockDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
public class PolygonHandler {
    private static final Logger logger = LoggerFactory.getLogger(PolygonHandler.class);

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

        LocalDate requestedDate;
        try {
            requestedDate = LocalDate.parse(date);
            if (requestedDate.isAfter(LocalDate.now())) {
                logger.error("InvalidDateException: The requested date is in the future.");
                throw new InvalidDateException("The requested date is in the future.");
            }
        } catch (InvalidDateException e) {
            return Mono.error(e);
        } catch (Exception e) {
            logger.error("Exception: Invalid date format", e);
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format", e));
        }

        return polygonService.getOpenClose(symbol, date, apiKey).flatMap(dailyOpenClose -> {
                    double initialPrice = dailyOpenClose.close();
                    long initialVolume = dailyOpenClose.volume();

                    Flux<CurrentStockInfo> dataStream = stockDataService.generateDataStream(symbol, initialPrice, initialVolume)
                            .onBackpressureBuffer(100, BufferOverflowStrategy.DROP_OLDEST);
                    return ServerResponse.ok()
                            .contentType(MediaType.TEXT_EVENT_STREAM)
                            .body(dataStream, CurrentStockInfo.class).log();
                }).log()
                .onErrorMap(e -> {
                    logger.error("Error processing request", e);
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request", e);
                });
    }
}
