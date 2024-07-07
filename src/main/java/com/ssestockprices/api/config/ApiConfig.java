package com.ssestockprices.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ApiConfig {
    @Value("${polygon.api.key}")
    private String polygonApiKey;
}

