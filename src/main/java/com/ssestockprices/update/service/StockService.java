package com.ssestockprices.service;

import com.ssestockprices.model.StockUpdateRequest;
import com.ssestockprices.model.StockUpdateResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.Stream;

@Service
public class StockService {
    Flux<StockUpdateResponse> update(StockUpdateRequest stockUpdateRequest) {
        return Flux.fromStream(
                Stream.generate(() -> new StockUpdateResponse(stockUpdateRequest.getName(),)
        );
    }

    private BigDecimal generateStockPrice() {
        
    }



}
