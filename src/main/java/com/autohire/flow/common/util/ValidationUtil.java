package com.autohire.flow.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validation utility methods for common validations.
 */
@Slf4j
@Component
public class ValidationUtil {
    
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    
    /**
     * Validates email format.
     * @param email email to validate
     * @return true if email is valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.matches(EMAIL_REGEX);
    }
    
    /**
     * Validates password strength.
     * @param password password to validate
     * @return true if password meets requirements (min 8 chars), false otherwise
     */
    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return true;
    }
    
    /**
     * Validates match score is within valid range.
     * @param score score to validate
     * @return true if score is between 0-100, false otherwise
     */
    public boolean isValidScore(Double score) {
        return score != null && score >= 0 && score <= 100;
    }
    
    /**
     * Validates file name is safe.
     * @param filename filename to validate
     * @return true if filename is safe, false otherwise
     */
    public boolean isValidFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }
        // Check for path traversal attempts
        return !filename.contains("..") && !filename.contains("/") && !filename.contains("\\");
    }
}
