package com.ev.billingservice.repository;

import com.ev.billingservice.model.BillingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillingPlanRepository extends JpaRepository<BillingPlan, UUID> {
    
    List<BillingPlan> findByIsActiveTrue();
    
    boolean existsByName(String name);
} 