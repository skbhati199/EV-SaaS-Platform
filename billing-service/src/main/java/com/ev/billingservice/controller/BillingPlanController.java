package com.ev.billingservice.controller;

import com.ev.billingservice.dto.BillingPlanDTO;
import com.ev.billingservice.service.BillingPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/plans")
@RequiredArgsConstructor
public class BillingPlanController {
    
    private final BillingPlanService billingPlanService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillingPlanDTO> createBillingPlan(@Valid @RequestBody BillingPlanDTO billingPlanDTO) {
        return new ResponseEntity<>(billingPlanService.createBillingPlan(billingPlanDTO), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BillingPlanDTO> getBillingPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(billingPlanService.getBillingPlanById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<BillingPlanDTO>> getAllBillingPlans() {
        return ResponseEntity.ok(billingPlanService.getAllBillingPlans());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<BillingPlanDTO>> getActiveBillingPlans() {
        return ResponseEntity.ok(billingPlanService.getActiveBillingPlans());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillingPlanDTO> updateBillingPlan(@PathVariable UUID id, @Valid @RequestBody BillingPlanDTO billingPlanDTO) {
        return ResponseEntity.ok(billingPlanService.updateBillingPlan(id, billingPlanDTO));
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateBillingPlan(@PathVariable UUID id) {
        billingPlanService.activateBillingPlan(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateBillingPlan(@PathVariable UUID id) {
        billingPlanService.deactivateBillingPlan(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBillingPlan(@PathVariable UUID id) {
        billingPlanService.deleteBillingPlan(id);
        return ResponseEntity.noContent().build();
    }
} 