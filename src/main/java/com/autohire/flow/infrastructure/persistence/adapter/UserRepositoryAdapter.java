package com.autohire.flow.infrastructure.persistence.adapter;

import com.autohire.flow.application.port.outgoing.UserPort;
import com.autohire.flow.domain.exception.UserNotSavedException;
import com.autohire.flow.domain.model.User;
import com.autohire.flow.infrastructure.persistence.entity.UserEntity;
import com.autohire.flow.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter implementing UserPort using JPA repository.
 * Bridges domain and infrastructure layers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserPort {
    
    private final JpaUserRepository jpaUserRepository;
    
    @Override
    public User save(User user) {
        try {
            UserEntity entity = UserEntity.builder()
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .name(user.getName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
            
            UserEntity saved = jpaUserRepository.save(entity);
            log.info("User saved successfully: {}", saved.getEmail());
            
            return toDomain(saved);
        } catch (Exception e) {
            log.error("Failed to save user: {}", user.getEmail(), e);
            throw new UserNotSavedException(user.getEmail(), e);
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
            .map(this::toDomain);
    }
    
    @Override
    public Optional<User> findById(Long userId) {
        return jpaUserRepository.findById(userId)
            .map(this::toDomain);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
    
    @Override
    public User update(User user) {
        UserEntity entity = jpaUserRepository.findById(user.getId())
            .orElseThrow(() -> new com.autohire.flow.domain.exception.DomainException(
                "User not found with ID: " + user.getId()
            ));
        
        entity.setEmail(user.getEmail());
        entity.setName(user.getName());
        entity.setIsActive(user.getIsActive());
        entity.setLastLogin(user.getLastLogin());
        
        UserEntity updated = jpaUserRepository.save(entity);
        log.info("User updated successfully: {}", updated.getEmail());
        
        return toDomain(updated);
    }
    
    private User toDomain(UserEntity entity) {
        return User.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .passwordHash(entity.getPasswordHash())
            .name(entity.getName())
            .role(entity.getRole())
            .isActive(entity.getIsActive())
            .lastLogin(entity.getLastLogin())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
