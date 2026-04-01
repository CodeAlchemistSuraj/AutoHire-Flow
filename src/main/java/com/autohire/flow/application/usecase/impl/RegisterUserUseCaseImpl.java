package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.RegisterUserUseCase;
import com.autohire.flow.application.port.outgoing.UserPort;
import com.autohire.flow.domain.model.User;
import com.autohire.flow.domain.exception.UserNotSavedException;
import com.autohire.flow.common.util.PasswordUtil;
import com.autohire.flow.common.util.JwtUtil;
import com.autohire.flow.common.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    
    private final UserPort userPort;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final ValidationUtil validationUtil;
    
    @Override
    public RegistrationResult execute(RegisterCommand command) {
        log.info("Registering new user with email: {}", command.email());
        
        // Validate input
        validationUtil.validateEmail(command.email());
        validationUtil.validatePassword(command.password());
        validationUtil.validateName(command.name());
        
        // Check if user already exists
        if (userPort.findByEmail(command.email()).isPresent()) {
            throw new UserNotSavedException("User already exists with email: " + command.email());
        }
        
        // Create user
        User user = User.builder()
            .email(command.email().toLowerCase())
            .passwordHash(passwordUtil.encode(command.password()))
            .name(command.name().trim())
            .role("USER")
            .isActive(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        
        // Save user
        User savedUser = userPort.save(user);
        
        if (savedUser == null || savedUser.getId() == null) {
            throw new UserNotSavedException("Failed to save user");
        }
        
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail());
        Long expiresIn = jwtUtil.getExpirationMs();
        
            return new RegistrationResult(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                token
            );

    }
}