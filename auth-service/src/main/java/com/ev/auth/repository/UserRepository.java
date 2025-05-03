package com.ev.auth.repository;

import com.ev.auth.model.Role;
import com.ev.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Find a user by username
     * @param username User's username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email
     * @param email User's email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by role
     * @param role User's role
     * @return List of users with the given role
     */
    List<User> findByRole(Role role);
    
    /**
     * Check if a user exists with the given username
     * @param username User's username
     * @return True if user exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if a user exists with the given email
     * @param email User's email address
     * @return True if user exists
     */
    boolean existsByEmail(String email);
}
