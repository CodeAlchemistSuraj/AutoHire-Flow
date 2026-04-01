package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.LoginUserUseCase;
import com.autohire.flow.application.port.outgoing.UserPort;
import com.autohire.flow.domain.model.User;
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
public class LoginUserUseCaseImpl implements LoginUserUseCase {
    
    private final UserPort userPort;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final ValidationUtil validationUtil;
    
    @Override
    public LoginResult execute(LoginCommand command) {
        log.info("Login attempt for email: {}", command.email());
        
        // Validate input
        validationUtil.validateEmail(command.email());
        
        // Find user
        User user = userPort.findByEmail(command.email().toLowerCase())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        // Verify password
        if (!passwordUtil.matches(command.password(), user.getPasswordHash())) {
            log.warn("Invalid password attempt for user: {}", command.email());
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        // Check if user is active
        if (!user.isEnabled()) {
            log.warn("Inactive user login attempt: {}", command.email());
            throw new IllegalStateException("Account is disabled. Please contact support.");
        }
        
        // Update last login
        user.recordLogin();
        userPort.save(user);
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        return new LoginResult(
            user.getId(),
            user.getEmail(),
            user.getName(),
            token
        );
    }
}