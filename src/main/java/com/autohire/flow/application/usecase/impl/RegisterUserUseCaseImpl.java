package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.RegisterUserUseCase;
import com.autohire.flow.application.port.outgoing.UserPort;
import com.autohire.flow.common.util.JwtUtil;
import com.autohire.flow.common.util.PasswordUtil;
import com.autohire.flow.common.util.ValidationUtil;
import com.autohire.flow.domain.model.User;
import com.autohire.flow.domain.exception.UserNotSavedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Register User Use Case Implementation
 * 
 * Handles user registration with email validation, password hashing,
 * JWT token generation, and user persistence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserPort userPort;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final ValidationUtil validationUtil;

    @Override
    @Transactional
    public RegistrationResult registerUser(RegisterCommand command) {
        log.info("Processing registration for email: {}", command.email());
        
        // Validate input
        validateRegistrationCommand(command);
        
        // Check if email already exists
        if (userPort.existsByEmail(command.email())) {
            log.warn("Registration failed - email already exists: {}", command.email());
            throw new IllegalArgumentException("Email already registered: " + command.email());
        }
        
        try {
            // Hash password
            String passwordHash = passwordUtil.encode(command.password());
            
            // Create domain model
            User user = new User(
                command.email(),
                passwordHash,
                command.name()
            );
            
            // Set initial status
            user.setActive(true);
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
            
            // Save user
            User savedUser = userPort.save(user);
            
            if (savedUser.getId() == null) {
                log.error("User not saved - returned null ID");
                throw new UserNotSavedException("User was not persisted properly");
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());
            Instant issuedAt = Instant.now();
            long expiresIn = 24 * 60 * 60; // 24 hours in seconds
            
            log.info("User registered successfully with ID: {}", savedUser.getId());
            
            return new RegistrationResult(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                token,
                expiresIn,
                issuedAt
            );
            
        } catch (UserNotSavedException e) {
            log.error("Failed to persist user with email: {}", command.email(), e);
            throw e;
        } catch (Exception e) {
            log.error("Registration failed for email: {}", command.email(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    /**
     * Validate registration command
     */
    private void validateRegistrationCommand(RegisterCommand command) {
        if (!validationUtil.isValidEmail(command.email())) {
            throw new IllegalArgumentException("Invalid email format: " + command.email());
        }
        
        if (!validationUtil.isStrongPassword(command.password())) {
            throw new IllegalArgumentException(
                "Password must be at least 8 characters with uppercase, lowercase, and numbers"
            );
        }
        
        if (command.name() == null || command.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
}
