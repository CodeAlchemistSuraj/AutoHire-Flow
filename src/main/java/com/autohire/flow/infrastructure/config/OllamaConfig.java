package com.autohire.flow.infrastructure.config;

import com.autohire.flow.infrastructure.ai.ollama.OllamaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for Ollama AI integration
 */
@Configuration
public class OllamaConfig {
    
    @Value("${ollama.embedding-model:nomic-embed-text}")
    private String embeddingModel;
    
    @Value("${ollama.chat-model:mistral}")
    private String chatModel;
    
    @Bean
    public OllamaClient ollamaClient(RestTemplate restTemplate) {
        return new OllamaClient(restTemplate, new com.fasterxml.jackson.databind.ObjectMapper());
    }
}
