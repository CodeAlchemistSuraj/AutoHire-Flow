package com.autohire.flow.application.dto.response;

import lombok.*;
import java.time.Instant;

/**
 * DTO for authentication response.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    
    private Long userId;
    
    private String email;
    
    private String name;
    
    private String token;
    
    private Long expiresIn;  // milliseconds
    
    private Instant issuedAt;
}
