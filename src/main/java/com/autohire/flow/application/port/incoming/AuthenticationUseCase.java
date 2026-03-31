package com.autohire.flow.application.port.incoming;

/**
 * Use case for authentication
 */
public interface AuthenticationUseCase {
    
    LoginResponse login(String email, String password);
    
    RegisterResponse register(String name, String email, String password);
    
    record LoginResponse(
        Long userId,
        String token,
        String name,
        String email
    ) {}
    
    record RegisterResponse(
        Long userId,
        String email,
        String message
    ) {}
}
