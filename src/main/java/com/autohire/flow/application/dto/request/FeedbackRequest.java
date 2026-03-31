package com.autohire.flow.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for feedback submission request.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    
    @NotNull(message = "Match Result ID is required")
    @Positive(message = "Match Result ID must be positive")
    private Long matchResultId;
    
    @NotBlank(message = "Feedback type is required")
    @Pattern(
        regexp = "TOO_SENIOR|NOT_INTERESTED|SALARY_LOW|OTHER",
        message = "Feedback type must be TOO_SENIOR, NOT_INTERESTED, SALARY_LOW, or OTHER"
    )
    private String feedbackType;
    
    @Size(max = 500, message = "Comments cannot exceed 500 characters")
    private String comments;
}
