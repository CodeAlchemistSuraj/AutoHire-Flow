package com.autohire.flow.application.dto.response;

import lombok.*;
import java.time.Instant;

/**
 * DTO for cover letter generation response.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoverLetterResponse {
    
    private Long coverLetterId;
    
    private String content;
    
    private Integer wordCount;
    
    private Integer paragraphCount;
    
    private String tone;
    
    private Instant generatedAt;
    
    private Boolean meetsMinimumRequirements;
}
