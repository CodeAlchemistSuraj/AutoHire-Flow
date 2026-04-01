package com.autohire.flow.domain.model;

import java.time.Instant;

/**
 * User domain entity representing a platform user.
 * Pure domain object with no Spring dependencies.
 */
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
    
    // Constructors
    public User() {
    }
    
    public User(String email, String passwordHash, String name, String role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.isActive = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    public User(Long id, String email, String passwordHash, String name, String role, Boolean isActive, 
                Instant lastLogin, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Builder pattern
    public static UserBuilder builder() {
        return new UserBuilder();
    }
    
    public static class UserBuilder {
        private Long id;
        private String email;
        private String passwordHash;
        private String name;
        private String role;
        private Boolean isActive;
        private Instant lastLogin;
        private Instant createdAt;
        private Instant updatedAt;
        
        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder role(String role) { this.role = role; return this; }
        public UserBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public UserBuilder lastLogin(Instant lastLogin) { this.lastLogin = lastLogin; return this; }
        public UserBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public UserBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public User build() {
            return new User(id, email, passwordHash, name, role, isActive, lastLogin, createdAt, updatedAt);
        }
    }
    
    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public Boolean getIsActive() { return isActive; }
    public Instant getLastLogin() { return lastLogin; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setLastLogin(Instant lastLogin) { this.lastLogin = lastLogin; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
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