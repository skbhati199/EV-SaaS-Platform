package com.ev.auth.repository;

import com.ev.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Find a user by email
     * @param email User's email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find a user by Keycloak ID
     * @param keycloakId Keycloak user ID
     * @return Optional containing the user if found
     */
    Optional<User> findByKeycloakId(String keycloakId);
    
    /**
     * Check if a user exists with the given email
     * @param email User's email address
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
}
