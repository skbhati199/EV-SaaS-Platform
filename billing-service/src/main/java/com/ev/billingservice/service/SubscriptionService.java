package com.ev.billingservice.service;

import com.ev.billingservice.dto.SubscriptionDTO;
import com.ev.billingservice.model.Subscription.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    
    SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO);
    
    SubscriptionDTO getSubscriptionById(UUID id);
    
    List<SubscriptionDTO> getSubscriptionsByUserId(UUID userId);
    
    List<SubscriptionDTO> getSubscriptionsByOrganizationId(UUID organizationId);
    
    SubscriptionDTO getActiveSubscriptionByUserId(UUID userId);
    
    SubscriptionDTO getActiveSubscriptionByOrganizationId(UUID organizationId);
    
    List<SubscriptionDTO> getSubscriptionsByStatus(SubscriptionStatus status);
    
    SubscriptionDTO updateSubscription(UUID id, SubscriptionDTO subscriptionDTO);
    
    SubscriptionDTO cancelSubscription(UUID id);
    
    SubscriptionDTO renewSubscription(UUID id);
    
    List<SubscriptionDTO> findExpiredSubscriptions();
    
    List<SubscriptionDTO> findSubscriptionsToRenew(LocalDateTime start, LocalDateTime end);
    
    void processAutoRenewals();
    
    void processSubscriptionExpiration();
} 