package com.ev.billingservice.repository;

import com.ev.billingservice.model.BillingSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillingSettingsRepository extends JpaRepository<BillingSettings, UUID> {
    
    Optional<BillingSettings> findByOrganizationId(UUID organizationId);
    
    boolean existsByOrganizationId(UUID organizationId);
} 