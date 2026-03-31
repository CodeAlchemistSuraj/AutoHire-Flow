package com.autohire.flow.infrastructure.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * REST Template Configuration
 * 
 * Configures RestTemplate bean for HTTP requests to external services
 * like Ollama API with appropriate timeouts and connection settings.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate bean with timeout configuration
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
    }
}
