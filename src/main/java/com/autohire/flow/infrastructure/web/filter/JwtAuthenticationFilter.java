package com.autohire.flow.infrastructure.web.filter;

import com.autohire.flow.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT Authentication Filter
 * 
 * Intercepts all HTTP requests and performs JWT token validation.
 * Extracts the token from Authorization header, validates it, and sets
 * the authentication in the SecurityContext if valid.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_LENGTH = 7;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromHeader(request);
            
            if (token != null && jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                Long userId = jwtUtil.getUserIdFromToken(token);
                
                log.debug("JWT token validated for email: {} with userId: {}", email, userId);
                
                // Create authentication token
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        null // No authorities for now - can be extended
                    );
                
                // Set additional details
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Store userId in request attribute for later use
                request.setAttribute("userId", userId);
                request.setAttribute("email", email);
                
            } else if (token != null) {
                log.warn("Invalid JWT token provided");
            }
            
        } catch (Exception e) {
            log.error("Failed to set user authentication in security context", e);
            // Continue filter chain even if exception occurs
            // GlobalExceptionHandler will catch any issues that occur downstream
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * 
     * Expected format: Bearer <token>
     * 
     * @param request HTTP request
     * @return JWT token or null if not found/invalid format
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_LENGTH);
        }
        
        return null;
    }

    /**
     * Specify endpoints that should skip JWT authentication
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip JWT validation for public endpoints
        return (method.equals("POST") && (
                path.equals("/api/v1/auth/register") ||
                path.equals("/api/v1/auth/login")
            )) ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.equals("/actuator/health");
    }
}
