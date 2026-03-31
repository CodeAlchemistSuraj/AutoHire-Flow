package com.autohire.flow.application.port.incoming;

import com.autohire.flow.domain.model.User;

/**
 * Use case for user registration.
 * According to Clean Architecture, ports should be input/output adapters.
 */
public interface RegisterUserUseCase {
    
    /**
     * Registers a new user in the system.
     * @param command registration command with email, password, and name
     * @return RegistrationResult containing user ID and JWT token
     * @throws IllegalArgumentException if email is already registered
     */
    RegistrationResult execute(RegisterCommand command);
    
    record RegisterCommand(String email, String password, String name) {}
    
    record RegistrationResult(Long userId, String email, String name, String token) {}
}
