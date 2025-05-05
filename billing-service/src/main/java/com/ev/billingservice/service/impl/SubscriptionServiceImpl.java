package com.ev.billingservice.service.impl;

import com.ev.billingservice.dto.BillingPlanDTO;
import com.ev.billingservice.dto.SubscriptionDTO;
import com.ev.billingservice.exception.BadRequestException;
import com.ev.billingservice.exception.ResourceNotFoundException;
import com.ev.billingservice.model.BillingPlan;
import com.ev.billingservice.model.Subscription;
import com.ev.billingservice.model.Subscription.BillingCycle;
import com.ev.billingservice.model.Subscription.SubscriptionStatus;
import com.ev.billingservice.repository.BillingPlanRepository;
import com.ev.billingservice.repository.SubscriptionRepository;
import com.ev.billingservice.service.NotificationService;
import com.ev.billingservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final BillingPlanRepository billingPlanRepository;
    private final NotificationService notificationService;
    
    @Override
    @Transactional
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        // Verify the plan exists and is active
        BillingPlan billingPlan = billingPlanRepository.findById(subscriptionDTO.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("BillingPlan", "id", subscriptionDTO.getPlanId()));
        
        if (!billingPlan.isActive()) {
            throw new BadRequestException("Cannot subscribe to inactive plan: " + billingPlan.getName());
        }
        
        // Check if there's already an active subscription for the user/organization
        if (subscriptionDTO.getStatus() == SubscriptionStatus.ACTIVE) {
            if (subscriptionRepository.findByUserIdAndStatus(subscriptionDTO.getUserId(), SubscriptionStatus.ACTIVE).isPresent()) {
                throw new BadRequestException("User already has an active subscription");
            }
            
            if (subscriptionRepository.findByOrganizationIdAndStatus(subscriptionDTO.getOrganizationId(), SubscriptionStatus.ACTIVE).isPresent()) {
                throw new BadRequestException("Organization already has an active subscription");
            }
        }
        
        // Set end date based on billing cycle if not provided
        if (subscriptionDTO.getEndDate() == null) {
            LocalDateTime endDate;
            if (subscriptionDTO.getBillingCycle() == BillingCycle.MONTHLY) {
                endDate = subscriptionDTO.getStartDate().plusMonths(1);
            } else {
                endDate = subscriptionDTO.getStartDate().plusYears(1);
            }
            subscriptionDTO.setEndDate(endDate);
        }
        
        Subscription subscription = mapToEntity(subscriptionDTO);
        subscription = subscriptionRepository.save(subscription);
        
        // Set the current price 
        subscription.setPrice(subscriptionDTO.getBillingCycle() == BillingCycle.MONTHLY ? 
                billingPlan.getPriceMonthly() : billingPlan.getPriceYearly());
        
        // Send notification for new subscription
        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            notificationService.sendSubscriptionCreatedNotification(subscription);
        }
        
        return mapToDTO(subscription);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubscriptionDTO getSubscriptionById(UUID id) {
        Subscription subscription = findSubscriptionById(id);
        return mapToDTO(subscription);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getSubscriptionsByUserId(UUID userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getSubscriptionsByOrganizationId(UUID organizationId) {
        List<Subscription> subscriptions = subscriptionRepository.findByOrganizationId(organizationId);
        return subscriptions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubscriptionDTO getActiveSubscriptionByUserId(UUID userId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active Subscription", "userId", userId));
        return mapToDTO(subscription);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubscriptionDTO getActiveSubscriptionForUser(UUID userId) {
        return subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .map(this::mapToDTO)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubscriptionDTO getActiveSubscriptionByOrganizationId(UUID organizationId) {
        Subscription subscription = subscriptionRepository.findByOrganizationIdAndStatus(organizationId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active Subscription", "organizationId", organizationId));
        return mapToDTO(subscription);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getSubscriptionsByStatus(SubscriptionStatus status) {
        List<Subscription> subscriptions = subscriptionRepository.findByStatus(status);
        return subscriptions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SubscriptionDTO updateSubscription(UUID id, SubscriptionDTO subscriptionDTO) {
        Subscription existingSubscription = findSubscriptionById(id);
        
        // Don't allow changing user or organization
        if (!existingSubscription.getUserId().equals(subscriptionDTO.getUserId())) {
            throw new BadRequestException("User ID cannot be changed");
        }
        
        if (!existingSubscription.getOrganizationId().equals(subscriptionDTO.getOrganizationId())) {
            throw new BadRequestException("Organization ID cannot be changed");
        }
        
        // Check if plan is changed
        boolean planChanged = !existingSubscription.getPlanId().equals(subscriptionDTO.getPlanId());
        if (planChanged) {
            BillingPlan newPlan = billingPlanRepository.findById(subscriptionDTO.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("BillingPlan", "id", subscriptionDTO.getPlanId()));
            
            if (!newPlan.isActive()) {
                throw new BadRequestException("Cannot change to inactive plan: " + newPlan.getName());
            }
        }
        
        // Update fields
        existingSubscription.setPlanId(subscriptionDTO.getPlanId());
        existingSubscription.setStatus(subscriptionDTO.getStatus());
        existingSubscription.setStartDate(subscriptionDTO.getStartDate());
        existingSubscription.setEndDate(subscriptionDTO.getEndDate());
        existingSubscription.setBillingCycle(subscriptionDTO.getBillingCycle());
        existingSubscription.setAutoRenew(subscriptionDTO.isAutoRenew());
        
        // Update price if plan or billing cycle changed
        if (planChanged || existingSubscription.getBillingCycle() != subscriptionDTO.getBillingCycle()) {
            BillingPlan plan = billingPlanRepository.findById(subscriptionDTO.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("BillingPlan", "id", subscriptionDTO.getPlanId()));
            
            BigDecimal price = subscriptionDTO.getBillingCycle() == BillingCycle.MONTHLY ? 
                    plan.getPriceMonthly() : plan.getPriceYearly();
            existingSubscription.setPrice(price);
        }
        
        existingSubscription = subscriptionRepository.save(existingSubscription);
        
        return mapToDTO(existingSubscription);
    }
    
    @Override
    @Transactional
    public SubscriptionDTO cancelSubscription(UUID id) {
        Subscription subscription = findSubscriptionById(id);
        
        if (subscription.getStatus() == SubscriptionStatus.CANCELED) {
            throw new BadRequestException("Subscription is already canceled");
        }
        
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setEndDate(LocalDateTime.now()); // End subscription immediately
        subscription = subscriptionRepository.save(subscription);
        
        // Send cancellation notification
        notificationService.sendSubscriptionCanceledNotification(subscription);
        
        return mapToDTO(subscription);
    }
    
    @Override
    @Transactional
    public SubscriptionDTO renewSubscription(UUID id) {
        Subscription subscription = findSubscriptionById(id);
        
        // Only active or expired subscriptions can be renewed
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE && subscription.getStatus() != SubscriptionStatus.EXPIRED) {
            throw new BadRequestException("Only active or expired subscriptions can be renewed");
        }
        
        LocalDateTime newStartDate = LocalDateTime.now();
        LocalDateTime newEndDate;
        
        if (subscription.getBillingCycle() == BillingCycle.MONTHLY) {
            newEndDate = newStartDate.plusMonths(1);
        } else {
            newEndDate = newStartDate.plusYears(1);
        }
        
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(newStartDate);
        subscription.setEndDate(newEndDate);
        subscription = subscriptionRepository.save(subscription);
        
        // Send new subscription notification
        notificationService.sendSubscriptionCreatedNotification(subscription);
        
        return mapToDTO(subscription);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> findExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(LocalDateTime.now());
        return expiredSubscriptions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> findSubscriptionsToRenew(LocalDateTime start, LocalDateTime end) {
        List<Subscription> subscriptionsToRenew = subscriptionRepository.findSubscriptionsToRenew(start, end);
        return subscriptionsToRenew.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    @Scheduled(cron = "${billing.subscription.renewal.cron:0 0 0 * * ?}") // Default: every day at midnight
    public void processAutoRenewals() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        
        List<Subscription> subscriptionsToRenew = subscriptionRepository.findSubscriptionsToRenew(now, tomorrow);
        
        for (Subscription subscription : subscriptionsToRenew) {
            try {
                LocalDateTime newEndDate;
                if (subscription.getBillingCycle() == BillingCycle.MONTHLY) {
                    newEndDate = subscription.getEndDate().plusMonths(1);
                } else {
                    newEndDate = subscription.getEndDate().plusYears(1);
                }
                
                subscription.setEndDate(newEndDate);
                subscriptionRepository.save(subscription);
                
                // Send notification about renewal
                notificationService.sendSubscriptionCreatedNotification(subscription);
                
                log.info("Auto-renewed subscription: {}", subscription.getId());
            } catch (Exception e) {
                log.error("Failed to auto-renew subscription: {}", subscription.getId(), e);
            }
        }
    }
    
    @Override
    @Transactional
    @Scheduled(cron = "${billing.subscription.expiration.cron:0 0 1 * * ?}") // Default: every day at 1 AM
    public void processSubscriptionExpiration() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(now);
        
        for (Subscription subscription : expiredSubscriptions) {
            try {
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(subscription);
                
                log.info("Marked subscription as expired: {}", subscription.getId());
            } catch (Exception e) {
                log.error("Failed to mark subscription as expired: {}", subscription.getId(), e);
            }
        }
    }
    
    private Subscription findSubscriptionById(UUID id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
    }
    
    private Subscription mapToEntity(SubscriptionDTO subscriptionDTO) {
        Subscription subscription = Subscription.builder()
                .userId(subscriptionDTO.getUserId())
                .organizationId(subscriptionDTO.getOrganizationId())
                .planId(subscriptionDTO.getPlanId())
                .status(subscriptionDTO.getStatus())
                .startDate(subscriptionDTO.getStartDate())
                .endDate(subscriptionDTO.getEndDate())
                .billingCycle(subscriptionDTO.getBillingCycle())
                .autoRenew(subscriptionDTO.isAutoRenew())
                .build();
        
        if (subscriptionDTO.getId() != null) {
            subscription.setId(subscriptionDTO.getId());
        }
        
        return subscription;
    }
    
    private SubscriptionDTO mapToDTO(Subscription subscription) {
        BillingPlanDTO billingPlanDTO = null;
        if (subscription.getBillingPlan() != null) {
            billingPlanDTO = mapBillingPlanToDTO(subscription.getBillingPlan());
        }
        
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .organizationId(subscription.getOrganizationId())
                .planId(subscription.getPlanId())
                .billingPlan(billingPlanDTO)
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .billingCycle(subscription.getBillingCycle())
                .autoRenew(subscription.isAutoRenew())
                .build();
    }
    
    private BillingPlanDTO mapBillingPlanToDTO(BillingPlan billingPlan) {
        return BillingPlanDTO.builder()
                .id(billingPlan.getId())
                .name(billingPlan.getName())
                .description(billingPlan.getDescription())
                .priceMonthly(billingPlan.getPriceMonthly())
                .priceYearly(billingPlan.getPriceYearly())
                .features(billingPlan.getFeatures())
                .isActive(billingPlan.isActive())
                .build();
    }
} 