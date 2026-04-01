package com.autohire.flow.infrastructure.web.controller;

import com.autohire.flow.application.dto.request.LoginRequest;
import com.autohire.flow.application.dto.request.RegisterRequest;
import com.autohire.flow.application.dto.response.ApiResponse;
import com.autohire.flow.application.dto.response.AuthResponse;
import com.autohire.flow.application.port.incoming.RegisterUserUseCase;
import com.autohire.flow.application.port.incoming.LoginUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Authentication Controller
 * 
 * Handles user registration and login operations.
 * Endpoints: POST /api/v1/auth/register, POST /api/v1/auth/login
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    /**
     * Register a new user
     * 
     * @param request Registration request with email, password, name
     * @return AuthResponse with userId, email, name, JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("User registration initiated for email: {}", request.getEmail());
        
        try {
            // Create registration command
            RegisterUserUseCase.RegisterCommand command = new RegisterUserUseCase.RegisterCommand(
                request.getEmail(),
                request.getPassword(),
                request.getName()
            );
            
            // Execute use case
            RegisterUserUseCase.RegistrationResult result = registerUserUseCase.execute(command);

            
            // Build response
            AuthResponse authResponse = AuthResponse.builder()
                .userId(result.userId())
                .email(result.email())
                .name(result.name())
                .token(result.token())
                .expiresIn(null)  // Not available from RegistrationResult
                .issuedAt(null)   // Not available from RegistrationResult
                .build();

           log.info("User registered successfully with ID: {}", result.userId());
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authResponse));
        } catch (Exception e) {
            log.error("Registration failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    /**
     * Login user
     * 
     * @param request Login request with email and password
     * @return AuthResponse with userId, email, name, JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        
        log.info("User login initiated for email: {}", request.getEmail());
        
        try {
            // Create login command
            LoginUserUseCase.LoginCommand command = new LoginUserUseCase.LoginCommand(
                request.getEmail(),
                request.getPassword()
            );
            
            // Execute use case
           LoginUserUseCase.LoginResult result = loginUserUseCase.execute(command);
            
            // Build response
AuthResponse authResponse = AuthResponse.builder()
                .userId(result.userId())
                .email(result.email())
                .name(result.name())
                .token(result.token())
                .expiresIn(null)  // Not available from LoginResult
                .issuedAt(null)   // Not available from LoginResult
                .build();
            
            log.info("User logged in successfully: {}", result.userId());
             
           return ResponseEntity
                .ok()
                .body(ApiResponse.success("Login successful", authResponse));
        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }
}
