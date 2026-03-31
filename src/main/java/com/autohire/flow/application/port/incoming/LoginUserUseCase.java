package com.autohire.flow.application.port.incoming;

/**
 * Use case for user login.
 */
public interface LoginUserUseCase {
    
    /**
     * Authenticates a user and generates JWT token.
     * @param command login credentials
     * @return LoginResult containing user ID and JWT token
     * @throws IllegalArgumentException if credentials are invalid
     */
    LoginResult execute(LoginCommand command);
    
    record LoginCommand(String email, String password) {}
    
    record LoginResult(Long userId, String email, String name, String token) {}
}
