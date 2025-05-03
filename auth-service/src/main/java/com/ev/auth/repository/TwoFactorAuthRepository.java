package com.ev.auth.repository;

import com.ev.auth.model.TwoFactorAuth;
import com.ev.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TwoFactorAuthRepository extends JpaRepository<TwoFactorAuth, UUID> {
    
    Optional<TwoFactorAuth> findByUser(User user);
    
    boolean existsByUser(User user);
} 