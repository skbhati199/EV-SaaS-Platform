package com.ev.userservice.repository;

import com.ev.userservice.model.RfidToken;
import com.ev.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RfidTokenRepository extends JpaRepository<RfidToken, UUID> {
    List<RfidToken> findByUser(User user);
    Optional<RfidToken> findByTokenValue(String tokenValue);
    List<RfidToken> findByUserAndIsActive(User user, boolean isActive);
    boolean existsByTokenValue(String tokenValue);
} 