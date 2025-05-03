package com.ev.auth.repository;

import com.ev.auth.model.PasswordlessAuth;
import com.ev.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordlessAuthRepository extends JpaRepository<PasswordlessAuth, UUID> {
    
    Optional<PasswordlessAuth> findByToken(String token);
    
    List<PasswordlessAuth> findByUser(User user);
    
    List<PasswordlessAuth> findByUserAndUsed(User user, boolean used);
    
    @Modifying
    @Query("DELETE FROM PasswordlessAuth p WHERE p.validUntil < ?1")
    int deleteExpiredTokens(LocalDateTime now);
} 