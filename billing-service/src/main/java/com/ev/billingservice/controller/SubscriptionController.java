package com.ev.billingservice.controller;

import com.ev.billingservice.dto.SubscriptionDTO;
import com.ev.billingservice.model.Subscription.SubscriptionStatus;
import com.ev.billingservice.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<SubscriptionDTO> createSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        return new ResponseEntity<>(subscriptionService.createSubscription(subscriptionDTO), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscriptionById(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUserId(userId));
    }
    
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByOrganizationId(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByOrganizationId(organizationId));
    }
    
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<SubscriptionDTO> getActiveSubscriptionByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscriptionByUserId(userId));
    }
    
    @GetMapping("/organization/{organizationId}/active")
    public ResponseEntity<SubscriptionDTO> getActiveSubscriptionByOrganizationId(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscriptionByOrganizationId(organizationId));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByStatus(@PathVariable SubscriptionStatus status) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByStatus(status));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<SubscriptionDTO> updateSubscription(@PathVariable UUID id, @Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        return ResponseEntity.ok(subscriptionService.updateSubscription(id, subscriptionDTO));
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionDTO> cancelSubscription(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
    }
    
    @PutMapping("/{id}/renew")
    public ResponseEntity<SubscriptionDTO> renewSubscription(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.renewSubscription(id));
    }
    
    @GetMapping("/expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionDTO>> findExpiredSubscriptions() {
        return ResponseEntity.ok(subscriptionService.findExpiredSubscriptions());
    }
    
    @PostMapping("/process-auto-renewals")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> processAutoRenewals() {
        subscriptionService.processAutoRenewals();
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/process-expirations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> processSubscriptionExpiration() {
        subscriptionService.processSubscriptionExpiration();
        return ResponseEntity.ok().build();
    }
} 