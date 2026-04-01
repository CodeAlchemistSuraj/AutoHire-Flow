package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.GenerateCoverLetterUseCase;
import com.autohire.flow.application.port.outgoing.ResumePort;
import com.autohire.flow.application.port.outgoing.JobPostingPort;
import com.autohire.flow.application.port.outgoing.CoverLetterPort;
import com.autohire.flow.application.port.outgoing.ChatPort;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.CoverLetter;
import com.autohire.flow.domain.exception.ResumeNotFoundException;
import com.autohire.flow.domain.exception.JobNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Generate Cover Letter Use Case Implementation
 * 
 * Handles AI-powered cover letter generation for job applications
 * using LLM (Ollama) integration with validation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateCoverLetterUseCaseImpl implements GenerateCoverLetterUseCase {

    private final ResumePort resumePort;
    private final JobPostingPort jobPostingPort;
    private final CoverLetterPort coverLetterPort;
    private final ChatPort chatPort;

    private static final int MIN_WORD_COUNT = 150;
    private static final int MAX_WORD_COUNT = 500;
    private static final int REQUIRED_PARAGRAPH_COUNT = 3;

    @Override
    @Transactional
    public GenerationResult execute(GenerationCommand command) {
        log.info("Generating cover letter for user: {} and job: {} with tone: {}", 
            command.userId(), command.jobId(), command.tone());
        
        try {
            // Fetch resume with Optional handling
            Resume resume = resumePort.findByUserId(command.userId())
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found for user: " + command.userId()));
            
            // Fetch job posting with Optional handling
            JobPosting job = jobPostingPort.findById(command.jobId())
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + command.jobId()));
            
            // Check if cover letter already exists
            Optional<CoverLetter> existing = coverLetterPort.findByUserAndJob(command.userId(), command.jobId());
            if (existing.isPresent()) {
                log.info("Cover letter already exists for user: {} and job: {}", 
                    command.userId(), command.jobId());
                CoverLetter existingLetter = existing.get();
                return new GenerationResult(
                    existingLetter.getId(),
                    existingLetter.getContent(),
                    existingLetter.getWordCount(),
                    existingLetter.getParagraphCount(),
                    existingLetter.getCreatedAt()
                );
            }
            
            // Call LLM to generate cover letter
            String generatedContent = chatPort.generateCoverLetter(resume, job, command.tone());
            
            // Validate generated content
            if (generatedContent == null || generatedContent.trim().isEmpty()) {
                throw new RuntimeException("LLM returned empty content");
            }
            
            // Analyze generated content
            int wordCount = countWords(generatedContent);
            int paragraphCount = countParagraphs(generatedContent);
            
            // Create cover letter domain model
            CoverLetter coverLetter = new CoverLetter(
                null,
                command.userId(),
                command.jobId(),
                generatedContent,
                command.tone(),
                Instant.now(),
                Instant.now()
            );
            
            // Save to database
            CoverLetter savedLetter = coverLetterPort.save(coverLetter);
            
            log.info("Cover letter generated successfully for user: {} and job: {} with ID: {}", 
                command.userId(), command.jobId(), savedLetter.getId());
            
            return new GenerationResult(
                savedLetter.getId(),
                generatedContent,
                wordCount,
                paragraphCount,
                savedLetter.getCreatedAt()
            );
            
        } catch (ResumeNotFoundException e) {
            log.error("Resume not found for user: {}", command.userId());
            throw new RuntimeException("Resume not found", e);
        } catch (JobNotFoundException e) {
            log.error("Job not found: {}", command.jobId());
            throw new RuntimeException("Job not found", e);
        } catch (Exception e) {
            log.error("Cover letter generation failed", e);
            throw new RuntimeException("Failed to generate cover letter: " + e.getMessage(), e);
        }
    }

    /**
     * Count words in text
     */
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    /**
     * Count paragraphs in text
     */
    private int countParagraphs(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        
        // Split by double newline or empty lines
        String[] paragraphs = text.split("\\n\\s*\\n");
        int count = 0;
        for (String para : paragraphs) {
            if (!para.trim().isEmpty()) {
                count++;
            }
        }
        
        return Math.max(count, 1);
    }
}