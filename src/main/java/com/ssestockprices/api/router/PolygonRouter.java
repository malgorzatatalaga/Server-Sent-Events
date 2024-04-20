package com.ssestockprices.api.router;

import com.ssestockprices.api.handler.PolygonHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PolygonRouter {
    @Bean
    public RouterFunction<ServerResponse> apiRoute(PolygonHandler polygonHandler) {
        return route(GET("/{symbol}/{date}").and(accept(MediaType.APPLICATION_JSON)), polygonHandler::getOpenClose);
    }
}
