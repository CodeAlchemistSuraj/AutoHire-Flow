package com.autohire.flow.domain.model;

import lombok.*;
import java.time.Instant;

/**
 * User domain entity representing a platform user.
 * Pure domain object with no Spring dependencies.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    
    private Long id;
    
    private String email;
    
    private String passwordHash;
    
    private String name;
    
    private String role;
    
    private Boolean isActive;
    
    private Instant lastLogin;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    /**
     * Checks if the user is actively able to use the platform.
     * @return true if user is active, false otherwise
     */
    public boolean isEnabled() {
        return this.isActive != null && this.isActive;
    }
    
    /**
     * Updates user profile information.
     * @param name new user name
     * @param email new email address
     */
    public void updateProfile(String name, String email) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }
        if (email != null && !email.isBlank()) {
            this.email = email.trim().toLowerCase();
        }
        this.updatedAt = Instant.now();
    }
    
    /**
     * Records the user's last login time.
     */
    public void recordLogin() {
        this.lastLogin = Instant.now();
        this.updatedAt = Instant.now();
    }
}
