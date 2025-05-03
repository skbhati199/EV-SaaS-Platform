package com.ev.billingservice.service;

import com.ev.billingservice.dto.BillingSettingsDTO;

import java.util.List;
import java.util.UUID;

public interface BillingSettingsService {
    
    BillingSettingsDTO createBillingSettings(BillingSettingsDTO billingSettingsDTO);
    
    BillingSettingsDTO getBillingSettingsById(UUID id);
    
    BillingSettingsDTO getBillingSettingsByOrganizationId(UUID organizationId);
    
    List<BillingSettingsDTO> getAllBillingSettings();
    
    BillingSettingsDTO updateBillingSettings(UUID id, BillingSettingsDTO billingSettingsDTO);
    
    boolean existsByOrganizationId(UUID organizationId);
} 