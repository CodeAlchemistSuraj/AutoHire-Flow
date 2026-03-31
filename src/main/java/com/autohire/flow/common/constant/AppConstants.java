package com.autohire.flow.common.constant;

/**
 * Application-wide constants.
 */
public class AppConstants {
    
    public static final String API_PREFIX = "/api/v1";
    
    public static final String AUTH_ENDPOINT = API_PREFIX + "/auth";
    public static final String RESUME_ENDPOINT = API_PREFIX + "/resume";
    public static final String MATCHER_ENDPOINT = API_PREFIX + "/matcher";
    public static final String COVER_LETTER_ENDPOINT = API_PREFIX + "/cover-letter";
    public static final String APPLICATIONS_ENDPOINT = API_PREFIX + "/applications";
    public static final String FEEDBACK_ENDPOINT = API_PREFIX + "/feedback";
    
    // User roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    
    // Match statuses
    public static final String MATCH_STATUS_PENDING = "PENDING";
    public static final String MATCH_STATUS_APPLIED = "APPLIED";
    public static final String MATCH_STATUS_REJECTED = "REJECTED";
    public static final String MATCH_STATUS_INTERVIEW = "INTERVIEW";
    
    // Resume parsing status
    public static final String PARSING_STATUS_SUCCESS = "SUCCESS";
    public static final String PARSING_STATUS_PARTIAL = "PARTIAL";
    public static final String PARSING_STATUS_FAILED = "FAILED";
    
    // Cover letter tones
    public static final String TONE_PROFESSIONAL = "PROFESSIONAL";
    public static final String TONE_ENTHUSIASTIC = "ENTHUSIASTIC";
    public static final String TONE_CONCISE = "CONCISE";
    
    // Feedback types
    public static final String FEEDBACK_TOO_SENIOR = "TOO_SENIOR";
    public static final String FEEDBACK_NOT_INTERESTED = "NOT_INTERESTED";
    public static final String FEEDBACK_SALARY_LOW = "SALARY_LOW";
    public static final String FEEDBACK_OTHER = "OTHER";  
    
    // Score thresholds
    public static final Double MIN_GOOD_MATCH_SCORE = 70.0;
    public static final Double MIN_EXCELLENT_MATCH_SCORE = 85.0;
    public static final Double MIN_MODERATE_MATCH_SCORE = 50.0;
    
    // Embedding dimensions
    public static final int EMBEDDING_DIMENSION = 768;
    
    // File upload limits
    public static final Long MAX_FILE_SIZE = 5 * 1024 * 1024L;  // 5MB
    public static final String ALLOWED_FILE_TYPE = "application/pdf";
    
    // Default pagination
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Integer MAX_PAGE_SIZE = 100;
    
    // Cache keys
    public static final String CACHE_MATCH_RESULT = "match:";
    public static final String CACHE_JOB_LISTING = "job:";
    public static final String CACHE_USER_SESSION = "session:";
    
    // Header constants
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_BEARER = "Bearer ";
    
    // Error codes
    public static final String ERROR_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERROR_FORBIDDEN = "FORBIDDEN";
    public static final String ERROR_NOT_FOUND = "NOT_FOUND";
    public static final String ERROR_BAD_REQUEST = "BAD_REQUEST";
    public static final String ERROR_INTERNAL = "INTERNAL_ERROR";
    
    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}
