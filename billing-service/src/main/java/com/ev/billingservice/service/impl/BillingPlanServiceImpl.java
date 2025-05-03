package com.ev.billingservice.service.impl;

import com.ev.billingservice.dto.BillingPlanDTO;
import com.ev.billingservice.exception.BadRequestException;
import com.ev.billingservice.exception.ResourceNotFoundException;
import com.ev.billingservice.model.BillingAuditLog;
import com.ev.billingservice.model.BillingPlan;
import com.ev.billingservice.repository.BillingAuditLogRepository;
import com.ev.billingservice.repository.BillingPlanRepository;
import com.ev.billingservice.service.BillingPlanService;
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
public class BillingPlanServiceImpl implements BillingPlanService {
    
    private final BillingPlanRepository billingPlanRepository;
    private final BillingAuditLogRepository billingAuditLogRepository;
    
    @Override
    @Transactional
    public BillingPlanDTO createBillingPlan(BillingPlanDTO billingPlanDTO) {
        if (billingPlanRepository.existsByName(billingPlanDTO.getName())) {
            throw new BadRequestException("Billing plan with name " + billingPlanDTO.getName() + " already exists");
        }
        
        BillingPlan billingPlan = mapToEntity(billingPlanDTO);
        billingPlan = billingPlanRepository.save(billingPlan);
        
        createAuditLog(billingPlan.getId(), "CREATE", "Created billing plan: " + billingPlan.getName());
        
        return mapToDTO(billingPlan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BillingPlanDTO getBillingPlanById(UUID id) {
        BillingPlan billingPlan = findBillingPlanById(id);
        return mapToDTO(billingPlan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillingPlanDTO> getAllBillingPlans() {
        List<BillingPlan> billingPlans = billingPlanRepository.findAll();
        return billingPlans.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillingPlanDTO> getActiveBillingPlans() {
        List<BillingPlan> activeBillingPlans = billingPlanRepository.findByIsActiveTrue();
        return activeBillingPlans.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public BillingPlanDTO updateBillingPlan(UUID id, BillingPlanDTO billingPlanDTO) {
        BillingPlan existingBillingPlan = findBillingPlanById(id);
        
        if (!existingBillingPlan.getName().equals(billingPlanDTO.getName()) 
                && billingPlanRepository.existsByName(billingPlanDTO.getName())) {
            throw new BadRequestException("Billing plan with name " + billingPlanDTO.getName() + " already exists");
        }
        
        existingBillingPlan.setName(billingPlanDTO.getName());
        existingBillingPlan.setDescription(billingPlanDTO.getDescription());
        existingBillingPlan.setPriceMonthly(billingPlanDTO.getPriceMonthly());
        existingBillingPlan.setPriceYearly(billingPlanDTO.getPriceYearly());
        existingBillingPlan.setFeatures(billingPlanDTO.getFeatures());
        
        BillingPlan updatedBillingPlan = billingPlanRepository.save(existingBillingPlan);
        
        createAuditLog(updatedBillingPlan.getId(), "UPDATE", "Updated billing plan: " + updatedBillingPlan.getName());
        
        return mapToDTO(updatedBillingPlan);
    }
    
    @Override
    @Transactional
    public void activateBillingPlan(UUID id) {
        BillingPlan billingPlan = findBillingPlanById(id);
        
        if (billingPlan.isActive()) {
            log.info("Billing plan with id {} is already active", id);
            return;
        }
        
        billingPlan.setActive(true);
        billingPlanRepository.save(billingPlan);
        
        createAuditLog(billingPlan.getId(), "ACTIVATE", "Activated billing plan: " + billingPlan.getName());
    }
    
    @Override
    @Transactional
    public void deactivateBillingPlan(UUID id) {
        BillingPlan billingPlan = findBillingPlanById(id);
        
        if (!billingPlan.isActive()) {
            log.info("Billing plan with id {} is already inactive", id);
            return;
        }
        
        billingPlan.setActive(false);
        billingPlanRepository.save(billingPlan);
        
        createAuditLog(billingPlan.getId(), "DEACTIVATE", "Deactivated billing plan: " + billingPlan.getName());
    }
    
    @Override
    @Transactional
    public void deleteBillingPlan(UUID id) {
        BillingPlan billingPlan = findBillingPlanById(id);
        
        // You might want to check if it's used in subscriptions before deletion
        // This is just a basic implementation
        
        String planName = billingPlan.getName();
        billingPlanRepository.delete(billingPlan);
        
        createAuditLog(id, "DELETE", "Deleted billing plan: " + planName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return billingPlanRepository.existsByName(name);
    }
    
    private BillingPlan findBillingPlanById(UUID id) {
        return billingPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BillingPlan", "id", id));
    }
    
    private BillingPlanDTO mapToDTO(BillingPlan billingPlan) {
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
    
    private BillingPlan mapToEntity(BillingPlanDTO billingPlanDTO) {
        return BillingPlan.builder()
                .name(billingPlanDTO.getName())
                .description(billingPlanDTO.getDescription())
                .priceMonthly(billingPlanDTO.getPriceMonthly())
                .priceYearly(billingPlanDTO.getPriceYearly())
                .features(billingPlanDTO.getFeatures())
                .isActive(billingPlanDTO.isActive())
                .build();
    }
    
    private void createAuditLog(UUID entityId, String action, String details) {
        BillingAuditLog auditLog = BillingAuditLog.builder()
                .entityType(BillingAuditLog.EntityType.BILLING_PLAN.name())
                .entityId(entityId)
                .action(action)
                .details(details)
                .build();
        
        billingAuditLogRepository.save(auditLog);
    }
} 