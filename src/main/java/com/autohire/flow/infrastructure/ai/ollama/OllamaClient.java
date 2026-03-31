package com.autohire.flow.infrastructure.ai.ollama;

import com.autohire.flow.domain.exception.AiServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * HTTP client for Ollama API communication
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OllamaClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;
    
    @Value("${ollama.timeout:30000}")
    private long timeout;
    
    @Value("${ollama.max-retries:3}")
    private int maxRetries;
    
    /**
     * Generate embeddings for text using Ollama
     */
    public float[] embed(String text, String model) {
        String url = baseUrl + "/api/embed";
        
        Map<String, Object> request = Map.of(
            "model", model,
            "input", text
        );
        
        try {
            String response = callOllamaApi(url, request);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("embeddings") && jsonNode.get("embeddings").isArray()) {
                JsonNode embeddingNode = jsonNode.get("embeddings").get(0);
                if (embeddingNode.isArray() && embeddingNode.size() > 0) {
                    float[] embeddings = new float[embeddingNode.size()];
                    for (int i = 0; i < embeddingNode.size(); i++) {
                        embeddings[i] = (float) embeddingNode.get(i).asDouble();
                    }
                    return embeddings;
                }
            }
            throw new AiServiceException("Invalid embedding response format");
        } catch (RestClientException e) {
            log.error("Failed to call Ollama embed API: {}", e.getMessage(), e);
            throw new AiServiceException("Failed to generate embeddings: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error processing embedding response: {}", e.getMessage(), e);
            throw new AiServiceException("Error processing embeddings: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate text using Ollama chat API
     */
    public String chat(String prompt, String model) {
        String url = baseUrl + "/api/generate";
        
        Map<String, Object> request = Map.of(
            "model", model,
            "prompt", prompt,
            "stream", false
        );
        
        try {
            String response = callOllamaApi(url, request);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("response")) {
                return jsonNode.get("response").asText();
            }
            throw new AiServiceException("Invalid chat response format");
        } catch (RestClientException e) {
            log.error("Failed to call Ollama chat API: {}", e.getMessage(), e);
            throw new AiServiceException("Failed to generate text: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error processing chat response: {}", e.getMessage(), e);
            throw new AiServiceException("Error processing response: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if model is available
     */
    public boolean isModelAvailable(String modelName) {
        String url = baseUrl + "/api/tags";
        
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("models") && jsonNode.get("models").isArray()) {
                for (JsonNode model : jsonNode.get("models")) {
                    if (model.has("name") && model.get("name").asText().contains(modelName)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("Failed to check model availability: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Pull model from Ollama registry
     */
    public void pullModel(String modelName) {
        String url = baseUrl + "/api/pull";
        
        Map<String, Object> request = Map.of("name", modelName);
        
        try {
            callOllamaApi(url, request);
            log.info("Successfully pulled model: {}", modelName);
        } catch (Exception e) {
            log.error("Failed to pull model {}: {}", modelName, e.getMessage(), e);
            throw new AiServiceException("Failed to pull model: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generic method to call Ollama API with retry logic
     */
    private String callOllamaApi(String url, Map<String, Object> request) {
        int retries = 0;
        Exception lastException = null;
        
        while (retries < maxRetries) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                String requestBody = objectMapper.writeValueAsString(request);
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                
                String response = restTemplate.postForObject(url, entity, String.class);
                log.debug("Ollama API response received");
                return response;
                
            } catch (RestClientException e) {
                lastException = e;
                retries++;
                if (retries < maxRetries) {
                    long delay = Math.min(1000L * (long) Math.pow(2, retries), 8000L);
                    log.warn("Ollama API call failed, retrying in {}ms (attempt {}/{})", 
                             delay, retries + 1, maxRetries);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new AiServiceException("Interrupted while retrying: " + ie.getMessage(), ie);
                    }
                }
            } catch (Exception e) {
                lastException = e;
                log.error("Unexpected error calling Ollama API: {}", e.getMessage(), e);
                throw new AiServiceException("Unexpected error: " + e.getMessage(), e);
            }
        }
        
        throw new AiServiceException("Ollama API call failed after " + maxRetries + " retries: " + 
                                     (lastException != null ? lastException.getMessage() : "Unknown error"), 
                                     lastException);
    }
}
