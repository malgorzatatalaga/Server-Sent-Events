package com.ssestockprices.api.handler;

import com.ssestockprices.api.model.DailyOpenClose;
import com.ssestockprices.api.service.PolygonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PolygonHandlerTest {

    private PolygonService polygonService;
    private PolygonHandler polygonHandler;

    @BeforeEach
    public void setUp() {
        polygonService = Mockito.mock(PolygonService.class);
        polygonHandler = new PolygonHandler(polygonService);
    }

    @Test
    public void testGetOpenCloseSuccessful() {
        DailyOpenClose doc = new DailyOpenClose(185.11, 185.85, "2023-01-01", 187.33, 179.25, 179.86, 182.4, "OK", "AAPL", 102527680);
        when(polygonService.getOpenClose(any(), any(), any())).thenReturn(Mono.just(doc));

        ServerRequest request = Mockito.mock(ServerRequest.class);
        Mockito.when(request.pathVariable("symbol")).thenReturn("AAPL");
        Mockito.when(request.pathVariable("date")).thenReturn("2023-01-01");

        Mono<ServerResponse> response = polygonHandler.getOpenClose(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        HttpStatus.OK.equals(serverResponse.statusCode()) &&  // Ensure using HttpStatus
                                MediaType.TEXT_PLAIN.equals(serverResponse.headers().getContentType()))
                .verifyComplete();


    }

    @Test
    public void testGetOpenCloseError() {
        when(polygonService.getOpenClose(any(), any(), any())).thenReturn(Mono.error(new RuntimeException("Internal Server Error")));

        ServerRequest request = Mockito.mock(ServerRequest.class);
        Mockito.when(request.pathVariable("symbol")).thenReturn("AAPL");
        Mockito.when(request.pathVariable("date")).thenReturn("2023-01-01");

        Mono<ServerResponse> response = polygonHandler.getOpenClose(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }
}

