package com.autohire.flow.infrastructure.ai.factory;

import com.autohire.flow.infrastructure.ai.service.OllamaEmbeddingService;
import com.autohire.flow.infrastructure.ai.service.OllamaChatService;
import com.autohire.flow.infrastructure.ai.parser.PdfResumeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory for managing AI services.
 * Centralizes instantiation and lifecycle management of AI-related services.
 * Provides a single point of access to embedding, chat, and parsing services.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AiServiceFactory {
    
    private final OllamaEmbeddingService embeddingService;
    private final OllamaChatService chatService;
    private final PdfResumeParser pdfParser;
    
    /**
     * Get the embedding service for generating vector embeddings.
     * @return OllamaEmbeddingService instance
     */
    public OllamaEmbeddingService getEmbeddingService() {
        log.debug("Retrieving embedding service");
        return embeddingService;
    }
    
    /**
     * Get the chat service for generating text responses.
     * @return OllamaChatService instance
     */
    public OllamaChatService getChatService() {
        log.debug("Retrieving chat service");
        return chatService;
    }
    
    /**
     * Get the PDF resume parser for parsing PDF documents.
     * @return PdfResumeParser instance
     */
    public PdfResumeParser getPdfParser() {
        log.debug("Retrieving PDF parser");
        return pdfParser;
    }
    
    /**
     * Verify that all required AI services are properly initialized.
     * @return true if all services are available, false otherwise
     */
    public boolean areServicesAvailable() {
        try {
            boolean embeddingAvailable = embeddingService != null;
            boolean chatAvailable = chatService != null;
            boolean parserAvailable = pdfParser != null;
            
            log.debug("Service availability - Embedding: {}, Chat: {}, Parser: {}",
                    embeddingAvailable, chatAvailable, parserAvailable);
            
            return embeddingAvailable && chatAvailable && parserAvailable;
        } catch (Exception e) {
            log.error("Error checking service availability", e);
            return false;
        }
    }
}
