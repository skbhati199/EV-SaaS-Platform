package com.ev.notificationservice.controller;

import com.ev.notificationservice.dto.NotificationDTO;
import com.ev.notificationservice.service.EventNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/templates")
@RequiredArgsConstructor
public class NotificationTemplateController {
    
    private final EventNotificationService eventNotificationService;
    
    @PostMapping("/verification")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<UUID> sendEmailVerification(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String verificationCode) {
            
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("verificationCode", verificationCode);
        
        NotificationDTO notification = NotificationDTO.builder()
                .userId(userId)
                .type("USER_VERIFICATION")
                .subject("Verify Your Email Address")
                .channel("EMAIL")
                .recipient(email)
                .build();
                
        UUID id = eventNotificationService.sendTemplatedEmailNotification(
                userId,
                email,
                "Verify Your Email Address",
                "email-verification",
                templateData,
                "USER_VERIFICATION"
        );
        
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }
    
    @PostMapping("/password-reset")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<UUID> sendPasswordReset(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String resetToken) {
            
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("resetToken", resetToken);
        
        NotificationDTO notification = NotificationDTO.builder()
                .userId(userId)
                .type("PASSWORD_RESET")
                .subject("Reset Your Password")
                .channel("EMAIL")
                .recipient(email)
                .build();
                
        UUID id = eventNotificationService.sendTemplatedEmailNotification(
                userId,
                email,
                "Reset Your Password",
                "password-reset",
                templateData,
                "PASSWORD_RESET"
        );
        
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }
    
    @PostMapping("/charging-started")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO')")
    public ResponseEntity<UUID> sendChargingStarted(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String stationName,
            @RequestParam String sessionId) {
            
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("stationName", stationName);
        templateData.put("sessionId", sessionId);
        
        NotificationDTO notification = NotificationDTO.builder()
                .userId(userId)
                .type("CHARGING_STARTED")
                .subject("Your Charging Session Has Started")
                .channel("EMAIL")
                .recipient(email)
                .build();
                
        UUID id = eventNotificationService.sendTemplatedEmailNotification(
                userId,
                email,
                "Your Charging Session Has Started",
                "charging-started",
                templateData,
                "CHARGING_STARTED"
        );
        
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }
    
    @PostMapping("/charging-completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO')")
    public ResponseEntity<UUID> sendChargingCompleted(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String stationName,
            @RequestParam String sessionId,
            @RequestParam String duration,
            @RequestParam String energy,
            @RequestParam String cost) {
            
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("stationName", stationName);
        templateData.put("sessionId", sessionId);
        templateData.put("duration", duration);
        templateData.put("energy", energy);
        templateData.put("cost", cost);
        
        NotificationDTO notification = NotificationDTO.builder()
                .userId(userId)
                .type("CHARGING_COMPLETED")
                .subject("Your Charging Session Is Complete")
                .channel("EMAIL")
                .recipient(email)
                .build();
                
        UUID id = eventNotificationService.sendTemplatedEmailNotification(
                userId,
                email,
                "Your Charging Session Is Complete",
                "charging-completed",
                templateData,
                "CHARGING_COMPLETED"
        );
        
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }
    
    @PostMapping("/payment-successful")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<UUID> sendPaymentSuccessful(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String amount,
            @RequestParam String invoiceNumber) {
            
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("amount", amount);
        templateData.put("invoiceNumber", invoiceNumber);
        
        NotificationDTO notification = NotificationDTO.builder()
                .userId(userId)
                .type("PAYMENT_SUCCESSFUL")
                .subject("Payment Successful")
                .channel("EMAIL")
                .recipient(email)
                .build();
                
        UUID id = eventNotificationService.sendTemplatedEmailNotification(
                userId,
                email,
                "Payment Successful",
                "payment-successful",
                templateData,
                "PAYMENT_SUCCESSFUL"
        );
        
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }
    
    @PostMapping("/payment-failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<UUID> sendPaymentFailed(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String amount,
            @RequestParam String invoiceNumber,
            @RequestParam String errorMessage) {
            
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("amount", amount);
        templateData.put("invoiceNumber", invoiceNumber);
        templateData.put("errorMessage", errorMessage);
        
        NotificationDTO notification = NotificationDTO.builder()
                .userId(userId)
                .type("PAYMENT_FAILED")
                .subject("Payment Failed")
                .channel("EMAIL")
                .recipient(email)
                .build();
                
        UUID id = eventNotificationService.sendTemplatedEmailNotification(
                userId,
                email,
                "Payment Failed",
                "payment-failed",
                templateData,
                "PAYMENT_FAILED"
        );
        
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }
} 