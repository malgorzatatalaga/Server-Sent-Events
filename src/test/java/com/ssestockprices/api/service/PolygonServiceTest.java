package com.ssestockprices.api.service;

import com.ssestockprices.api.model.DailyOpenClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolygonServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PolygonService polygonService;

    @Test
    public void testGetOpenClose() {
        String symbol = "AAPL";
        String date = "2023-05-10";
        String apiKey = "test-api-key";
        DailyOpenClose expectedData = new DailyOpenClose(150.50, 155.25, "2023-05-10", 156.00, 150.00, 152.75, 149.50, "success", "AAPL", 50000);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("https://api.polygon.io/v1/open-close/{symbol}/{date}?adjusted=true&apiKey={apiKey}", symbol, date, apiKey)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DailyOpenClose.class)).thenReturn(Mono.just(expectedData));

        Mono<DailyOpenClose> result = polygonService.getOpenClose(symbol, date, apiKey);

        StepVerifier.create(result)
                .expectNext(expectedData)
                .verifyComplete();

        verify(webClientBuilder).build();
        verify(requestHeadersSpec).retrieve();
    }
}
