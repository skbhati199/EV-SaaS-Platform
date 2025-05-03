package com.ev.billingservice.service.impl;

import com.ev.billingservice.dto.BillingSettingsDTO;
import com.ev.billingservice.exception.BadRequestException;
import com.ev.billingservice.exception.ResourceNotFoundException;
import com.ev.billingservice.model.BillingAuditLog;
import com.ev.billingservice.model.BillingSettings;
import com.ev.billingservice.repository.BillingAuditLogRepository;
import com.ev.billingservice.repository.BillingSettingsRepository;
import com.ev.billingservice.service.BillingSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingSettingsServiceImpl implements BillingSettingsService {
    
    private final BillingSettingsRepository billingSettingsRepository;
    private final BillingAuditLogRepository billingAuditLogRepository;
    
    @Override
    @Transactional
    public BillingSettingsDTO createBillingSettings(BillingSettingsDTO billingSettingsDTO) {
        if (billingSettingsRepository.existsByOrganizationId(billingSettingsDTO.getOrganizationId())) {
            throw new BadRequestException("Billing settings already exist for organization with ID: " + billingSettingsDTO.getOrganizationId());
        }
        
        BillingSettings billingSettings = mapToEntity(billingSettingsDTO);
        billingSettings = billingSettingsRepository.save(billingSettings);
        
        createAuditLog(billingSettings.getId(), "CREATE", "Created billing settings for organization: " + billingSettings.getOrganizationId());
        
        return mapToDTO(billingSettings);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BillingSettingsDTO getBillingSettingsById(UUID id) {
        BillingSettings billingSettings = findBillingSettingsById(id);
        return mapToDTO(billingSettings);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BillingSettingsDTO getBillingSettingsByOrganizationId(UUID organizationId) {
        BillingSettings billingSettings = billingSettingsRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("BillingSettings", "organizationId", organizationId));
        
        return mapToDTO(billingSettings);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillingSettingsDTO> getAllBillingSettings() {
        List<BillingSettings> billingSettings = billingSettingsRepository.findAll();
        return billingSettings.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public BillingSettingsDTO updateBillingSettings(UUID id, BillingSettingsDTO billingSettingsDTO) {
        BillingSettings existingBillingSettings = findBillingSettingsById(id);
        
        if (!existingBillingSettings.getOrganizationId().equals(billingSettingsDTO.getOrganizationId())) {
            if (billingSettingsRepository.existsByOrganizationId(billingSettingsDTO.getOrganizationId())) {
                throw new BadRequestException("Cannot change organization ID to " + billingSettingsDTO.getOrganizationId() 
                        + " as billing settings already exist for this organization");
            }
        }
        
        existingBillingSettings.setOrganizationId(billingSettingsDTO.getOrganizationId());
        existingBillingSettings.setBillingEmail(billingSettingsDTO.getBillingEmail());
        existingBillingSettings.setTaxId(billingSettingsDTO.getTaxId());
        existingBillingSettings.setBillingAddress(billingSettingsDTO.getBillingAddress());
        existingBillingSettings.setCurrency(billingSettingsDTO.getCurrency());
        
        BillingSettings updatedBillingSettings = billingSettingsRepository.save(existingBillingSettings);
        
        createAuditLog(updatedBillingSettings.getId(), "UPDATE", "Updated billing settings for organization: " + updatedBillingSettings.getOrganizationId());
        
        return mapToDTO(updatedBillingSettings);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrganizationId(UUID organizationId) {
        return billingSettingsRepository.existsByOrganizationId(organizationId);
    }
    
    private BillingSettings findBillingSettingsById(UUID id) {
        return billingSettingsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BillingSettings", "id", id));
    }
    
    private BillingSettingsDTO mapToDTO(BillingSettings billingSettings) {
        return BillingSettingsDTO.builder()
                .id(billingSettings.getId())
                .organizationId(billingSettings.getOrganizationId())
                .billingEmail(billingSettings.getBillingEmail())
                .taxId(billingSettings.getTaxId())
                .billingAddress(billingSettings.getBillingAddress())
                .currency(billingSettings.getCurrency())
                .build();
    }
    
    private BillingSettings mapToEntity(BillingSettingsDTO billingSettingsDTO) {
        return BillingSettings.builder()
                .organizationId(billingSettingsDTO.getOrganizationId())
                .billingEmail(billingSettingsDTO.getBillingEmail())
                .taxId(billingSettingsDTO.getTaxId())
                .billingAddress(billingSettingsDTO.getBillingAddress())
                .currency(billingSettingsDTO.getCurrency())
                .build();
    }
    
    private void createAuditLog(UUID entityId, String action, String details) {
        BillingAuditLog auditLog = BillingAuditLog.builder()
                .entityType(BillingAuditLog.EntityType.BILLING_SETTINGS.name())
                .entityId(entityId)
                .action(action)
                .details(details)
                .build();
        
        billingAuditLogRepository.save(auditLog);
    }
} 