package com.ev.billingservice.controller;

import com.ev.billingservice.dto.BillingSettingsDTO;
import com.ev.billingservice.service.BillingSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/settings")
@RequiredArgsConstructor
public class BillingSettingsController {
    
    private final BillingSettingsService billingSettingsService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<BillingSettingsDTO> createBillingSettings(@Valid @RequestBody BillingSettingsDTO billingSettingsDTO) {
        return new ResponseEntity<>(billingSettingsService.createBillingSettings(billingSettingsDTO), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BillingSettingsDTO> getBillingSettingsById(@PathVariable UUID id) {
        return ResponseEntity.ok(billingSettingsService.getBillingSettingsById(id));
    }
    
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<BillingSettingsDTO> getBillingSettingsByOrganizationId(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(billingSettingsService.getBillingSettingsByOrganizationId(organizationId));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BillingSettingsDTO>> getAllBillingSettings() {
        return ResponseEntity.ok(billingSettingsService.getAllBillingSettings());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<BillingSettingsDTO> updateBillingSettings(
            @PathVariable UUID id, @Valid @RequestBody BillingSettingsDTO billingSettingsDTO) {
        return ResponseEntity.ok(billingSettingsService.updateBillingSettings(id, billingSettingsDTO));
    }
    
    @GetMapping("/exists/{organizationId}")
    public ResponseEntity<Boolean> existsByOrganizationId(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(billingSettingsService.existsByOrganizationId(organizationId));
    }
} 