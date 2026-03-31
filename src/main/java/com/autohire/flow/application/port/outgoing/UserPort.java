package com.autohire.flow.application.port.outgoing;

import com.autohire.flow.domain.model.User;
import java.util.Optional;

/**
 * Output port for user persistence operations.
 * Abstraction between domain and infrastructure layers.
 */
public interface UserPort {
    
    /**
     * Persists a user in the repository.
     * @param user user to save
     * @return saved user with generated ID
     */
    User save(User user);
    
    /**
     * Finds a user by email.
     * @param email user email
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Finds a user by ID.
     * @param userId user ID
     * @return Optional containing user if found
     */
    Optional<User> findById(Long userId);
    
    /**
     * Checks if a user exists with given email.
     * @param email email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Updates a user.
     * @param user user with updated fields
     * @return updated user
     */
    User update(User user);
}
