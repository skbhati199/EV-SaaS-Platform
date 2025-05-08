package com.ev.auth.repository;

import com.ev.auth.model.RefreshToken;
import com.ev.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    
    /**
     * Find a refresh token by token string
     * @param token The token string
     * @return The refresh token entity
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Find all refresh tokens for a user
     * @param user The user
     * @return The list of refresh tokens
     */
    List<RefreshToken> findAllByUser(User user);
    
    /**
     * Delete all refresh tokens for a user
     * @param user The user
     */
    @Modifying
    void deleteByUser(User user);
    
    /**
     * Count non-revoked tokens for a user
     * @param user The user
     * @param revoked The revoked status
     * @return The count of tokens
     */
    long countByUserAndRevoked(User user, boolean revoked);
} 