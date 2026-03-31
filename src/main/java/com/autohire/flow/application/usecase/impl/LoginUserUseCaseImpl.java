package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.LoginUserUseCase;
import com.autohire.flow.application.port.outgoing.UserPort;
import com.autohire.flow.common.util.JwtUtil;
import com.autohire.flow.common.util.PasswordUtil;
import com.autohire.flow.common.util.ValidationUtil;
import com.autohire.flow.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Login User Use Case Implementation
 * 
 * Handles user authentication with email/password validation,
 * login timestamp recording, and JWT token generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserUseCaseImpl implements LoginUserUseCase {

    private final UserPort userPort;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final ValidationUtil validationUtil;

    @Override
    @Transactional
    public LoginResult loginUser(LoginCommand command) {
        log.info("Processing login for email: {}", command.email());
        
        // Validate input
        if (!validationUtil.isValidEmail(command.email())) {
            log.warn("Login failed - invalid email format: {}", command.email());
            throw new BadCredentialsException("Invalid email format");
        }
        
        try {
            // Find user by email
            User user = userPort.findByEmail(command.email());
            
            // Check if user is active
            if (!user.isEnabled()) {
                log.warn("Login failed - user account inactive: {}", command.email());
                throw new BadCredentialsException("User account is inactive");
            }
            
            // Verify password
            if (!passwordUtil.matches(command.password(), user.getPasswordHash())) {
                log.warn("Login failed - invalid password for email: {}", command.email());
                throw new BadCredentialsException("Invalid email or password");
            }
            
            // Record login timestamp
            user.recordLogin(Instant.now());
            user.setUpdatedAt(Instant.now());
            
            // Update user with new login time
            User updatedUser = userPort.update(user);
            
            // Generate JWT token
            String token = jwtUtil.generateToken(updatedUser.getEmail(), updatedUser.getId());
            Instant issuedAt = Instant.now();
            long expiresIn = 24 * 60 * 60; // 24 hours in seconds
            
            log.info("User logged in successfully: {}", updatedUser.getId());
            
            return new LoginResult(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getName(),
                token,
                expiresIn,
                issuedAt
            );
            
        } catch (BadCredentialsException e) {
            log.warn("Login authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Login failed for email: {}", command.email(), e);
            throw new BadCredentialsException("Authentication failed: " + e.getMessage(), e);
        }
    }
}
