package com.ev.billingservice.dto;

import com.ev.billingservice.model.Subscription.BillingCycle;
import com.ev.billingservice.model.Subscription.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    
    private UUID id;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
    
    @NotNull(message = "Plan ID is required")
    private UUID planId;
    
    // Aliases for planId to maintain compatibility with existing code
    public UUID getBillingPlanId() {
        return planId;
    }
    
    public void setBillingPlanId(UUID billingPlanId) {
        this.planId = billingPlanId;
    }
    
    private BillingPlanDTO billingPlan;
    
    @NotNull(message = "Status is required")
    private SubscriptionStatus status;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    @NotNull(message = "Billing cycle is required")
    private BillingCycle billingCycle;
    
    private boolean autoRenew;
} 