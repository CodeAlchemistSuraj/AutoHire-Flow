package com.autohire.flow.common.aspect;

import com.autohire.flow.common.annotation.RateLimited;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Optional;

/**
 * AOP aspect for rate limiting
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitingAspect {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Before("@annotation(rateLimited)")
    public void checkRateLimit(JoinPoint joinPoint, RateLimited rateLimited) {
        String key = generateKey(rateLimited.key());
        
        // Use Redis-backed rate limiting
        String bucketKey = "rate_limit:" + key;
        Long currentCount = (Long) redisTemplate.opsForValue().get(bucketKey);
        
        if (currentCount == null) {
            currentCount = 0L;
            redisTemplate.opsForValue().set(bucketKey, 0L, 
                Duration.ofMinutes(rateLimited.windowMinutes()));
        }
        
        if (currentCount >= rateLimited.limit()) {
            log.warn("Rate limit exceeded for key: {}", key);
            throw new RuntimeException(rateLimited.message());
        }
        
        redisTemplate.opsForValue().increment(bucketKey);
    }
    
    /**
     * Generate rate limit key based on identifier
     */
    private String generateKey(String keyType) {
        Optional<ServletRequestAttributes> attributes = 
            Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        
        if (attributes.isEmpty()) {
            return "unknown";
        }
        
        HttpServletRequest request = attributes.get().getRequest();
        
        return switch (keyType) {
            case "userId" -> extractUserId();
            case "ip" -> getClientIp(request);
            default -> "default";
        };
    }
    
    /**
     * Extract user ID from request context
     */
    private String extractUserId() {
        Optional<ServletRequestAttributes> attributes = 
            Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        
        if (attributes.isEmpty()) {
            return "anonymous";
        }
        
        HttpServletRequest request = attributes.get().getRequest();
        Object userId = request.getAttribute("userId");
        
        return userId != null ? userId.toString() : "anonymous";
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
