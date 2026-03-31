package com.autohire.flow.application.port.outgoing;

import java.util.List;

/**
 * Output port for embedding generation (AI service).
 * Abstraction for Ollama or other embedding services.
 */
public interface EmbeddingPort {
    
    /**
     * Generates embedding for a single text.
     * @param text text to embed
     * @return 768-dimensional float array (nomic-embed-text)
     */
    float[] embed(String text);
    
    /**
     * Generates embeddings for multiple texts in batch.
     * @param texts list of texts to embed
     * @return list of 768-dimensional float arrays
     */
    List<float[]> embedBatch(List<String> texts);
    
    /**
     * Checks if embedding service is available.
     * @return true if service is healthy, false otherwise
     */
    boolean isHealthy();
}
