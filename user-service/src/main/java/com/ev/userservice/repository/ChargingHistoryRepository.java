package com.ev.userservice.repository;

import com.ev.userservice.model.ChargingHistory;
import com.ev.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChargingHistoryRepository extends JpaRepository<ChargingHistory, UUID> {
    List<ChargingHistory> findByUser(User user);
    Page<ChargingHistory> findByUser(User user, Pageable pageable);
    Optional<ChargingHistory> findBySessionId(UUID sessionId);
    List<ChargingHistory> findByUserAndStartTimeBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
} 