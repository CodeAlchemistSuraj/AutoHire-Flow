package com.autohire.flow.infrastructure.ai.parser;

import com.autohire.flow.domain.model.Resume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data class for parsed resume information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParsedResumeData {
    
    private String rawText;
    private String email;
    private String phone;
    private List<String> skills;
    private List<Resume.Experience> experiences;
    private String education;
    
    public boolean isValid() {
        return rawText != null && !rawText.trim().isEmpty() &&
               (skills != null && !skills.isEmpty() || 
                experiences != null && !experiences.isEmpty() ||
                education != null && !education.trim().isEmpty());
    }
}
