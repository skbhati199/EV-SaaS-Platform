package com.ev.billingservice.repository;

import com.ev.billingservice.model.Subscription;
import com.ev.billingservice.model.Subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    
    List<Subscription> findByUserId(UUID userId);
    
    List<Subscription> findByOrganizationId(UUID organizationId);
    
    List<Subscription> findByStatus(SubscriptionStatus status);
    
    Optional<Subscription> findByUserIdAndStatus(UUID userId, SubscriptionStatus status);
    
    Optional<Subscription> findByOrganizationIdAndStatus(UUID organizationId, SubscriptionStatus status);
    
    @Query("SELECT s FROM Subscription s WHERE s.endDate < ?1 AND s.status = 'ACTIVE'")
    List<Subscription> findExpiredSubscriptions(LocalDateTime now);
    
    @Query("SELECT s FROM Subscription s WHERE s.endDate BETWEEN ?1 AND ?2 AND s.status = 'ACTIVE' AND s.autoRenew = true")
    List<Subscription> findSubscriptionsToRenew(LocalDateTime start, LocalDateTime end);
} 