package com.autohire.flow.common.annotation;

import java.lang.annotation.*;

/**
 * Annotation for rate limiting on controller methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimited {
    
    /**
     * Rate limit requests per time window
     */
    int limit() default 100;
    
    /**
     * Time window in minutes
     */
    int windowMinutes() default 1;
    
    /**
     * Rate limit key (e.g., "userId", "ip")
     */
    String key() default "userId";
    
    /**
     * Error message when rate limit exceeded
     */
    String message() default "Rate limit exceeded";
}
