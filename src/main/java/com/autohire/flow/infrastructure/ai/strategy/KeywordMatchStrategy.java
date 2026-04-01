package com.autohire.flow.infrastructure.ai.strategy;

import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.service.MatchStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Keyword-based matching strategy.
 * Implementation of the MatchStrategy domain interface.
 */
@Component
@Slf4j
public class KeywordMatchStrategy implements MatchStrategy {
    
    @Override
    public double calculateScore(Resume resume, JobPosting job) {
        Set<String> resumeSkills = new HashSet<>(resume.getSkills() != null ? resume.getSkills() : List.of());
        Set<String> jobRequiredSkills = new HashSet<>(job.getRequiredSkills() != null ? job.getRequiredSkills() : List.of());
        
        // Calculate required skills match (100%)
        double requiredMatchPercentage = calculateMatchPercentage(resumeSkills, jobRequiredSkills);
        double requiredScore = requiredMatchPercentage * 100;
        
        log.debug("Keyword match score: {} (required: {})", 
                  requiredScore, requiredScore);
        return Math.min(100.0, Math.max(0.0, requiredScore));
    }
    
    /**
     * Calculate percentage of job skills found in resume
     */
    private double calculateMatchPercentage(Set<String> resumeSkills, Set<String> jobSkills) {
        if (jobSkills.isEmpty()) {
            return 100.0; // Full match if no required skills
        }
        
        long matchCount = jobSkills.stream()
            .filter(skill -> isSkillMatched(skill, resumeSkills))
            .count();
        
        return (double) matchCount / jobSkills.size() * 100;
    }
    
    /**
     * Check if job skill is matched in resume skills
     */
    private boolean isSkillMatched(String jobSkill, Set<String> resumeSkills) {
        String normalizedJobSkill = normalize(jobSkill);
        
        return resumeSkills.stream()
            .anyMatch(resumeSkill -> normalize(resumeSkill).contains(normalizedJobSkill) ||
                                     normalizedJobSkill.contains(normalize(resumeSkill)));
    }
    
    /**
     * Normalize skill name for comparison
     */
    private String normalize(String skill) {
        return skill.toLowerCase()
            .replaceAll("[^a-z0-9]", "")
            .replaceAll("\\+\\+", "pp");
    }
    
    @Override
    public String getName() {
        return "KEYWORD";
    }
}
