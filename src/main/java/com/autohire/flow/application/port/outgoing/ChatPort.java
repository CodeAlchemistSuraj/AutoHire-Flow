package com.autohire.flow.application.port.outgoing;

import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.model.JobPosting;

/**
 * Output port for AI-powered chat/text generation (cover letter generation).
 * Abstraction for Ollama or other LLM services.
 */
public interface ChatPort {
    
    /**
     * Generates a tailored cover letter for a job.
     * @param resume user's resume
     * @param jobPosting target job posting
     * @param tone tone of the letter (PROFESSIONAL, ENTHUSIASTIC, CONCISE)
     * @return generated cover letter text
     */
    String generateCoverLetter(Resume resume, JobPosting jobPosting, String tone);
    
    /**
     * Checks if chat service is available.
     * @return true if service is healthy, false otherwise
     */
    boolean isHealthy();
}
