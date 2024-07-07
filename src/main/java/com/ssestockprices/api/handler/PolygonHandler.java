package com.ssestockprices.api.handler;

import com.ssestockprices.api.config.ApiConfig;
import com.ssestockprices.api.exception.InvalidDateException;
import com.ssestockprices.api.model.CurrentStockInfo;
import com.ssestockprices.api.service.PolygonService;
import com.ssestockprices.streaming.service.StockDataService;
import lombok.Getter;
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
import java.time.format.DateTimeParseException;

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

        return validateDate(date)
                .flatMap(validDate -> polygonService.getOpenClose(symbol, validDate, apiKey)
                        .flatMap(dailyOpenClose -> {
                            double initialPrice = dailyOpenClose.close();
                            long initialVolume = dailyOpenClose.volume();

                            Flux<CurrentStockInfo> dataStream = stockDataService.generateDataStream(symbol, initialPrice, initialVolume)
                                    .onBackpressureBuffer(100, BufferOverflowStrategy.DROP_OLDEST);

                            return ServerResponse.ok()
                                    .contentType(MediaType.TEXT_EVENT_STREAM)
                                    .body(dataStream, CurrentStockInfo.class);
                        }))
                .onErrorResume(e -> {
                    logger.error("Error processing request", e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorResponse("Error processing request", e.getMessage()));
                });
    }

    private Mono<String> validateDate(String date) {
        return Mono.defer(() -> {
            try {
                LocalDate requestedDate = LocalDate.parse(date);
                if (requestedDate.isAfter(LocalDate.now())) {
                    logger.error("InvalidDateException: The requested date is in the future.");
                    return Mono.error(new InvalidDateException("The requested date is in the future."));
                }
                return Mono.just(date);
            } catch (DateTimeParseException e) {
                logger.error("Exception: Invalid date format", e);
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format", e));
            }
        });
    }

    @Getter
    record ErrorResponse(String message, String details) {

    }
}
