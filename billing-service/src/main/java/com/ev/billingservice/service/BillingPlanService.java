package com.ev.billingservice.service;

import com.ev.billingservice.dto.BillingPlanDTO;

import java.util.List;
import java.util.UUID;

public interface BillingPlanService {
    
    BillingPlanDTO createBillingPlan(BillingPlanDTO billingPlanDTO);
    
    BillingPlanDTO getBillingPlanById(UUID id);
    
    List<BillingPlanDTO> getAllBillingPlans();
    
    List<BillingPlanDTO> getActiveBillingPlans();
    
    BillingPlanDTO updateBillingPlan(UUID id, BillingPlanDTO billingPlanDTO);
    
    void activateBillingPlan(UUID id);
    
    void deactivateBillingPlan(UUID id);
    
    void deleteBillingPlan(UUID id);
    
    boolean existsByName(String name);
} 