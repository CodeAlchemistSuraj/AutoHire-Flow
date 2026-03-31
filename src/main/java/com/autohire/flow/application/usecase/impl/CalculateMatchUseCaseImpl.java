package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.CalculateMatchUseCase;
import com.autohire.flow.application.port.outgoing.ResumePort;
import com.autohire.flow.application.port.outgoing.JobPostingPort;
import com.autohire.flow.application.port.outgoing.MatchResultPort;
import com.autohire.flow.common.util.ValidationUtil;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.MatchResult;
import com.autohire.flow.domain.exception.MatchCalculationException;
import com.autohire.flow.domain.exception.ResumeNotFoundException;
import com.autohire.flow.domain.exception.JobNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Calculate Match Use Case Implementation
 * 
 * Performs semantic matching between resume and job posting using:
 * 1. Vector embedding cosine similarity
 * 2. Skill matching
 * 3. Experience validation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalculateMatchUseCaseImpl implements CalculateMatchUseCase {

    private final ResumePort resumePort;
    private final JobPostingPort jobPostingPort;
    private final MatchResultPort matchResultPort;
    private final ValidationUtil validationUtil;

    private static final double MIN_EMBEDDING_SCORE = 0.5;
    private static final int MIN_SKILL_MATCH_THRESHOLD = 2;

    @Override
    @Transactional
    public MatchResult calculateMatch(MatchCommand command) {
        log.info("Calculating match for user: {} and job: {}", command.userId(), command.jobId());
        
        try {
            // Fetch resume
            Resume resume = resumePort.findByUserId(command.userId());
            
            // Fetch job posting
            JobPosting job = jobPostingPort.findById(command.jobId());
            
            // Check if job is active
            if (job.isExpired(Instant.now())) {
                log.warn("Job posting has expired: {}", command.jobId());
                throw new IllegalArgumentException("Job posting has expired");
            }
            
            // Check if existing match result exists
            var existingMatch = matchResultPort.findByUserAndJob(command.userId(), command.jobId());
            
            // Calculate match score
            int matchScore = calculateScore(resume, job);
            
            // Determine quality level
            String qualityLevel = determineQualityLevel(matchScore);
            
            // Extract matching and missing skills
            List<String> matchingSkills = getMatchingSkills(resume, job);
            List<String> missingSkills = getMissingSkills(resume, job);
            
            // Generate explanation
            String explanation = generateExplanation(matchScore, matchingSkills, missingSkills);
            
            // Create or update match result
            MatchResult match;
            if (existingMatch.isPresent()) {
                match = existingMatch.get();
                match.setScore(matchScore);
                match.setQualityLevel(qualityLevel);
                match.setMatchingSkills(matchingSkills);
                match.setMissingSkills(missingSkills);
                match.setExplanation(explanation);
                match.setUpdatedAt(Instant.now());
                match = matchResultPort.update(match);
                log.info("Match result updated for user: {} and job: {}", command.userId(), command.jobId());
            } else {
                match = new MatchResult(
                    null,
                    command.userId(),
                    command.jobId(),
                    matchScore
                );
                match.setQualityLevel(qualityLevel);
                match.setMatchingSkills(matchingSkills);
                match.setMissingSkills(missingSkills);
                match.setExplanation(explanation);
                match.setStatus("PENDING");
                match.setCreatedAt(Instant.now());
                match.setUpdatedAt(Instant.now());
                match = matchResultPort.save(match);
                log.info("New match result created for user: {} and job: {} with score: {}", 
                    command.userId(), command.jobId(), matchScore);
            }
            
            return match;
            
        } catch (ResumeNotFoundException e) {
            log.error("Resume not found for user: {}", command.userId());
            throw new MatchCalculationException("Resume not found for user", e);
        } catch (JobNotFoundException e) {
            log.error("Job not found: {}", command.jobId());
            throw new MatchCalculationException("Job not found", e);
        } catch (Exception e) {
            log.error("Match calculation failed", e);
            throw new MatchCalculationException("Failed to calculate match: " + e.getMessage(), e);
        }
    }

    /**
     * Calculate match score (0-100) using multiple factors
     */
    private int calculateScore(Resume resume, JobPosting job) {
        double score = 0;
        
        // 1. Vector embedding similarity (40% weight)
        double embeddingSimilarity = calculateCosineSimilarity(resume.getEmbedding(), job.getEmbedding());
        score += embeddingSimilarity * 40;
        
        // 2. Skill matching (40% weight)
        List<String> matchingSkills = getMatchingSkills(resume, job);
        List<String> requiredSkills = job.getRequiredSkills();
        double skillMatchRatio = requiredSkills.isEmpty() ? 1.0 : 
            (double) matchingSkills.size() / requiredSkills.size();
        score += Math.min(skillMatchRatio, 1.0) * 40;
        
        // 3. Experience validation (20% weight)
        double experienceScore = validateExperience(resume, job);
        score += experienceScore * 20;
        
        return Math.min(100, Math.max(0, (int) Math.round(score)));
    }

    /**
     * Calculate cosine similarity between two embedding vectors
     */
    private double calculateCosineSimilarity(double[] vec1, double[] vec2) {
        if (vec1 == null || vec2 == null || vec1.length == 0 || vec2.length == 0) {
            return 0.0;
        }
        
        if (vec1.length != vec2.length) {
            log.warn("Vector dimensions don't match: {} vs {}", vec1.length, vec2.length);
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        
        double denominator = Math.sqrt(norm1) * Math.sqrt(norm2);
        return denominator == 0 ? 0.0 : dotProduct / denominator;
    }

    /**
     * Validate experience match (0-1 scale)
     */
    private double validateExperience(Resume resume, JobPosting job) {
        try {
            long experienceYears = resume.getTotalExperienceYears();
            
            // Assume job requires some experience (simplified)
            // In production, job posting would have minExperience field
            if (experienceYears >= 1) {
                return 1.0;
            } else if (experienceYears >= 0.5) {
                return 0.7;
            } else {
                return 0.3;
            }
        } catch (Exception e) {
            log.warn("Could not validate experience: {}", e.getMessage());
            return 0.5;
        }
    }

    /**
     * Get matching skills between resume and job
     */
    private List<String> getMatchingSkills(Resume resume, JobPosting job) {
        Set<String> resumeSkills = new HashSet<>(resume.getSkills());
        Set<String> jobSkills = new HashSet<>(job.getRequiredSkills());
        
        List<String> matching = new ArrayList<>();
        for (String skill : jobSkills) {
            if (resumeSkills.contains(skill)) {
                matching.add(skill);
            }
        }
        
        return matching;
    }

    /**
     * Get missing skills
     */
    private List<String> getMissingSkills(Resume resume, JobPosting job) {
        Set<String> resumeSkills = new HashSet<>(resume.getSkills());
        Set<String> jobSkills = new HashSet<>(job.getRequiredSkills());
        
        List<String> missing = new ArrayList<>();
        for (String skill : jobSkills) {
            if (!resumeSkills.contains(skill)) {
                missing.add(skill);
            }
        }
        
        return missing;
    }

    /**
     * Determine quality level based on score
     */
    private String determineQualityLevel(int score) {
        if (score >= 80) return "EXCELLENT";
        if (score >= 60) return "GOOD";
        if (score >= 40) return "MODERATE";
        return "LOW";
    }

    /**
     * Generate human-readable explanation
     */
    private String generateExplanation(int score, List<String> matchingSkills, List<String> missingSkills) {
        StringBuilder explanation = new StringBuilder();
        
        explanation.append("Match Score: ").append(score).append("/100. ");
        
        if (!matchingSkills.isEmpty()) {
            explanation.append("Matching skills: ")
                .append(String.join(", ", matchingSkills)).append(". ");
        }
        
        if (!missingSkills.isEmpty()) {
            explanation.append("Missing skills: ")
                .append(String.join(", ", missingSkills)).append(".");
        }
        
        return explanation.toString();
    }
}
