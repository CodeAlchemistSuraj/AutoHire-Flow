package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.UploadResumeUseCase;
import com.autohire.flow.application.port.outgoing.ResumePort;
import com.autohire.flow.application.port.outgoing.EmbeddingPort;
import com.autohire.flow.application.port.outgoing.FileStoragePort;
import com.autohire.flow.common.util.ValidationUtil;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.exception.ResumeParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Upload Resume Use Case Implementation
 * 
 * Handles resume file validation, parsing, embedding generation,
 * storage, and persistence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadResumeUseCaseImpl implements UploadResumeUseCase {

    private final ResumePort resumePort;
    private final EmbeddingPort embeddingPort;
    private final FileStoragePort fileStoragePort;
    private final ValidationUtil validationUtil;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Override
    @Transactional
    public UploadResult execute(UploadCommand command) {
        log.info("Processing resume upload for user: {}", command.userId());
        
        MultipartFile file = command.file();
        
        // Validate file
        validateResumeFile(file);
        
        try {
            // Delete existing resume if present
            if (resumePort.existsByUserId(command.userId())) {
                log.info("Deleting existing resume for user: {}", command.userId());
                Optional<Resume> existingResume = resumePort.findByUserId(command.userId());
                existingResume.ifPresent(resume -> resumePort.delete(resume.getId()));
            }
            
            // Parse resume content (simple text extraction for demo)
            String resumeContent = parseResumeFile(file.getBytes(), file.getContentType());
            
            // Extract skills from resume (simplified - can be enhanced with NLP)
            List<String> skills = extractSkills(resumeContent);
            
            // Generate embedding for semantic search
            float[] embedding = embeddingPort.embed(resumeContent);
            
            // Store file
            String fileStoragePath = fileStoragePort.store(file, command.userId());
            
            // Create resume domain model
            Resume resume = new Resume();
            resume.setUserId(command.userId());
            resume.setOriginalFileName(file.getOriginalFilename());
            resume.setS3Key(fileStoragePath);
            resume.setParsedText(resumeContent);
            resume.setSkills(skills);
            resume.setEmbedding(embedding);
            resume.setUploadedAt(Instant.now());
            resume.setCreatedAt(Instant.now());
            resume.setUpdatedAt(Instant.now());
            
            // Save to database
            Resume savedResume = resumePort.save(resume);
            
            log.info("Resume uploaded successfully for user: {} with ID: {}", 
                command.userId(), savedResume.getId());
            
            return new UploadResult(
                savedResume.getId(),
                skills.size(),
                "SUCCESS",
                skills
            );
            
        } catch (ResumeParseException e) {
            log.error("Resume parsing failed for user: {}", command.userId(), e);
            throw e;
        } catch (IOException e) {
            log.error("File reading failed for user: {}", command.userId(), e);
            throw new RuntimeException("Failed to read file content", e);
        } catch (Exception e) {
            log.error("Resume upload failed for user: {}", command.userId(), e);
            throw new RuntimeException("Resume upload failed: " + e.getMessage(), e);
        }
    }

    /**
     * Validate resume file
     */
    private void validateResumeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed (5MB)");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                "Invalid file type. Supported: PDF, DOC, DOCX"
            );
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || !validationUtil.isSafeFileName(fileName)) {
            throw new IllegalArgumentException("Invalid file name");
        }
    }

    /**
     * Parse resume file content (simplified)
     * In production, use Apache PDFBox or similar for proper parsing
     */
    private String parseResumeFile(byte[] fileContent, String contentType) {
        try {
            // For demo purposes, convert bytes to string
            // In production, use proper PDF/DOCX parsing libraries
            String content = new String(fileContent);
            
            if (content.length() == 0) {
                throw new ResumeParseException("Failed to extract text from resume file");
            }
            
            return content;
            
        } catch (Exception e) {
            log.error("Resume parsing error", e);
            throw new ResumeParseException("Failed to parse resume: " + e.getMessage(), e);
        }
    }

    /**
     * Extract skills from resume content (simplified regex-based)
     * In production, use NLP for better skill extraction
     */
    private List<String> extractSkills(String resumeContent) {
        // Common tech skills - simplified extraction
        List<String> commonSkills = Arrays.asList(
            "Java", "Python", "JavaScript", "TypeScript", "Go", "Rust",
            "Spring Boot", "Django", "React", "Vue", "Angular",
            "PostgreSQL", "MySQL", "MongoDB", "Redis",
            "Docker", "Kubernetes", "AWS", "Azure", "GCP",
            "REST", "GraphQL", "Microservices", "Git",
            "Agile", "Scrum", "CI/CD", "Linux"
        );
        
        return resumeContent.lines()
            .flatMap(line -> commonSkills.stream()
                .filter(skill -> line.contains(skill))
                .map(skill -> skill))
            .distinct()
            .toList();
    }
}