package com.autohire.flow.infrastructure.ai.service;

import com.autohire.flow.application.port.outgoing.EmbeddingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ollama Embedding Service
 * 
 * Implements EmbeddingPort interface using Ollama local inference
 * with nomic-embed-text model for 768-dimensional embeddings.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaEmbeddingService implements EmbeddingPort {

    private final RestTemplate restTemplate;

    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${ollama.embedding-model:nomic-embed-text}")
    private String embeddingModel;

    private static final String EMBED_ENDPOINT = "/api/embed";
    private static final int EMBEDDING_DIMENSION = 768;

    @Override
    public double[] embed(String text) {
        log.debug("Generating embedding for text length: {}", text.length());
        
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }
        
        try {
            String url = ollamaUrl + EMBED_ENDPOINT;
            
            // Build request
            Map<String, Object> request = new HashMap<>();
            request.put("model", embeddingModel);
            request.put("input", text);
            
            // Call Ollama API
            EmbeddingResponse response = restTemplate.postForObject(
                url,
                request,
                EmbeddingResponse.class
            );
            
            if (response == null || response.embeddings == null || response.embeddings.isEmpty()) {
                log.error("Empty embedding response from Ollama");
                throw new RuntimeException("Failed to generate embedding");
            }
            
            double[] embedding = response.embeddings.get(0);
            
            if (embedding.length != EMBEDDING_DIMENSION) {
                log.warn("Embedding dimension mismatch: expected {}, got {}", 
                    EMBEDDING_DIMENSION, embedding.length);
            }
            
            log.debug("Embedding generated successfully with dimension: {}", embedding.length);
            return embedding;
            
        } catch (RestClientException e) {
            log.error("Ollama service error: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to Ollama service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Embedding generation failed", e);
            throw new RuntimeException("Failed to generate embedding: " + e.getMessage(), e);
        }
    }

    @Override
    public List<double[]> embedBatch(List<String> texts) {
        log.info("Generating embeddings for {} texts", texts.size());
        
        return texts.stream()
            .map(this::embed)
            .toList();
    }

    @Override
    public boolean isHealthy() {
        try {
            String url = ollamaUrl + "/api/tags";
            TagsResponse response = restTemplate.getForObject(url, TagsResponse.class);
            
            boolean healthy = response != null && response.models != null && 
                response.models.stream().anyMatch(m -> m.name.contains(embeddingModel));
            
            log.debug("Ollama health check: {}", healthy);
            return healthy;
            
        } catch (Exception e) {
            log.warn("Ollama health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Response class for Ollama embedding API
     */
    public static class EmbeddingResponse {
        public List<double[]> embeddings;
    }

    /**
     * Response class for Ollama tags/models API
     */
    public static class TagsResponse {
        public List<ModelTag> models;
    }

    public static class ModelTag {
        public String name;
    }
}
