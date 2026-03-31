package com.autohire.flow.infrastructure.ai.service;

import com.autohire.flow.application.port.outgoing.ChatPort;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.model.JobPosting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ollama Chat Service
 * 
 * Implements ChatPort interface using Ollama local inference
 * for AI-powered cover letter generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaChatService implements ChatPort {

    private final RestTemplate restTemplate;

    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${ollama.chat-model:mistral}")
    private String chatModel;

    private static final String CHAT_ENDPOINT = "/api/chat";
    private static final int MAX_RESPONSE_LENGTH = 2000;

    @Override
    public String generateCoverLetter(Resume resume, JobPosting job, String tone) {
        log.info("Generating cover letter for resume: {} and job: {} with tone: {}", 
            resume.getId(), job.getId(), tone);
        
        try {
            // Build system prompt
            String systemPrompt = buildSystemPrompt(tone);
            
            // Build user message
            String userMessage = buildUserMessage(resume, job, tone);
            
            // Call LLM
            String response = callOllamaChat(systemPrompt, userMessage);
            
            // Clean and validate response
            String coverLetter = sanitizeCoverLetter(response);
            
            log.debug("Cover letter generated with length: {}", coverLetter.length());
            return coverLetter;
            
        } catch (Exception e) {
            log.error("Cover letter generation failed", e);
            throw new RuntimeException("Failed to generate cover letter: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isHealthy() {
        try {
            String url = ollamaUrl + "/api/tags";
            OllamaEmbeddingService.TagsResponse response = restTemplate.getForObject(
                url, 
                OllamaEmbeddingService.TagsResponse.class
            );
            
            boolean healthy = response != null && response.models != null && 
                response.models.stream().anyMatch(m -> m.name.contains(chatModel));
            
            log.debug("Ollama chat service health check: {}", healthy);
            return healthy;
            
        } catch (Exception e) {
            log.warn("Ollama chat service health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Build system prompt for cover letter generation
     */
    private String buildSystemPrompt(String tone) {
        return switch (tone.toUpperCase()) {
            case "PROFESSIONAL" -> """
                You are a professional cover letter writer. Generate a formal, business-appropriate cover letter.
                Focus on professional achievements, relevant skills, and why the candidate is a good fit.
                Be concise and impactful.""";
                
            case "ENTHUSIASTIC" -> """
                You are an enthusiastic career coach. Generate an engaging, energetic cover letter.
                Show genuine interest and passion for the role and company.
                Be positive and motivating while remaining professional.""";
                
            case "CONCISE" -> """
                You are a concise communicator. Generate a brief, straight-to-the-point cover letter.
                Avoid unnecessary elaboration. Focus on key qualifications and interest.
                Be clear and efficient.""";
                
            default -> """
                Generate a professional cover letter that highlights the candidate's qualifications
                and expresses interest in the position.""";
        };
    }

    /**
     * Build user message with context
     */
    private String buildUserMessage(Resume resume, JobPosting job, String tone) {
        return String.format("""
            Write a cover letter for this job application:
            
            Candidate Skills: %s
            
            Job Title: %s
            Company: %s
            Job Description: %s
            
            Generate exactly 3 paragraphs with:
            - Paragraph 1: Opening statement showing interest
            - Paragraph 2: Relevant skills and experience
            - Paragraph 3: Closing with enthusiasm
            
            Keep it between 150-500 words. Tone: %s
            """,
            String.join(", ", resume.getSkills()),
            job.getTitle(),
            job.getCompanyName(),
            job.getDescription(),
            tone
        );
    }

    /**
     * Call Ollama chat API
     */
    private String callOllamaChat(String systemPrompt, String userMessage) throws RestClientException {
        String url = ollamaUrl + CHAT_ENDPOINT;
        
        // Build request
        Map<String, Object> request = buildChatRequest(systemPrompt, userMessage);
        
        // Call Ollama API
        ChatResponse response = restTemplate.postForObject(url, request, ChatResponse.class);
        
        if (response == null || response.message == null || response.message.content == null) {
            log.error("Empty chat response from Ollama");
            throw new RuntimeException("Failed to generate response");
        }
        
        return response.message.content;
    }

    /**
     * Build chat request for Ollama
     */
    private Map<String, Object> buildChatRequest(String systemPrompt, String userMessage) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", chatModel);
        request.put("stream", false);
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        // System message
        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemPrompt);
        messages.add(sysMsg);
        
        // User message
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        request.put("messages", messages);
        
        return request;
    }

    /**
     * Sanitize and validate generated cover letter
     */
    private String sanitizeCoverLetter(String content) {
        if (content == null) {
            return "";
        }
        
        // Trim and clean
        String sanitized = content.strip();
        
        // Truncate if too long
        if (sanitized.length() > MAX_RESPONSE_LENGTH) {
            sanitized = sanitized.substring(0, MAX_RESPONSE_LENGTH).strip();
            // Find last complete paragraph
            int lastPeriod = sanitized.lastIndexOf('.');
            if (lastPeriod > 0) {
                sanitized = sanitized.substring(0, lastPeriod + 1);
            }
        }
        
        return sanitized;
    }

    /**
     * Response class for Ollama chat API
     */
    public static class ChatResponse {
        public Message message;
        public String model;
        public long created_at;
    }

    public static class Message {
        public String role;
        public String content;
    }
}
