package com.ev.billingservice.controller;

import com.ev.billingservice.dto.PaymentMethodDTO;
import com.ev.billingservice.model.PaymentMethod.PaymentType;
import com.ev.billingservice.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {
    
    private final PaymentMethodService paymentMethodService;
    
    @PostMapping
    public ResponseEntity<PaymentMethodDTO> createPaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return new ResponseEntity<>(paymentMethodService.createPaymentMethod(paymentMethodDTO), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodDTO> getPaymentMethodById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethodsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodsByUserId(userId));
    }
    
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<PaymentMethodDTO> getDefaultPaymentMethodForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentMethodService.getDefaultPaymentMethodForUser(userId));
    }
    
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethodsByUserIdAndType(
            @PathVariable UUID userId, @PathVariable PaymentType type) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodsByUserIdAndType(userId, type));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(
            @PathVariable UUID id, @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(id, paymentMethodDTO));
    }
    
    @PutMapping("/{id}/set-default")
    public ResponseEntity<PaymentMethodDTO> setDefaultPaymentMethod(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentMethodService.setDefaultPaymentMethod(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable UUID id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }
} 