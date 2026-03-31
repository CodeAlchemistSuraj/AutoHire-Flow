package com.autohire.flow.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * DTO for cover letter generation request.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoverLetterRequest {
    
    @NotNull(message = "Job ID is required")
    @Positive(message = "Job ID must be positive")
    private Long jobId;
    
    @NotBlank(message = "Tone is required")
    @Pattern(
        regexp = "PROFESSIONAL|ENTHUSIASTIC|CONCISE",
        message = "Tone must be PROFESSIONAL, ENTHUSIASTIC, or CONCISE"
    )
    private String tone;
}
