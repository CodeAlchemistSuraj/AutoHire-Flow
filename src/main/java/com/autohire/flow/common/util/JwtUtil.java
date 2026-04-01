package com.autohire.flow.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility for JWT token generation and validation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
    /**
     * Generates a JWT token for a user.
     * @param userId user ID
     * @param email user email
     * @return JWT token string
     */
    public String generateToken(Long userId, String email) {
        return Jwts.builder()
            .subject(email)
            .claim("userId", userId)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), Jwts.SIG.HS512)
            .compact();
    }
    
    /**
     * Extracts user ID from JWT token.
     * @param token JWT token
     * @return user ID
     */
    public Long extractUserId(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("userId", Long.class);
    }
    
    /**
     * Extracts email from JWT token.
     * @param token JWT token
     * @return email
     */
    public String extractEmail(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
    
    /**
     * Validates a JWT token.
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if token is expired.
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
            
            return expiration.before(new Date());
        } catch (JwtException e) {
            log.error("Error extracting expiration: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * Get email from token (alias for extractEmail)
     */
    public String getEmailFromToken(String token) {
        return extractEmail(token);
    }
    
    /**
     * Get user ID from token (alias for extractUserId)
     */
    public Long getUserIdFromToken(String token) {
        return extractUserId(token);
    }
    
    /**
     * Get expiration in milliseconds
     */
    public Long getExpirationMs() {
        return expiration;
    }
}