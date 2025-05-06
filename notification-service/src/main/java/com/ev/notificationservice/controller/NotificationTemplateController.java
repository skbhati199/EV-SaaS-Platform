package com.ev.notificationservice.controller;

import com.ev.notificationservice.dto.NotificationDTO;
import com.ev.notificationservice.model.NotificationType;
import com.ev.notificationservice.service.EventNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications/templates")
@RequiredArgsConstructor
public class NotificationTemplateController {
    
    private final EventNotificationService eventNotificationService;
    
    @PostMapping("/verification")
    public ResponseEntity<NotificationDTO> sendVerificationEmail(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String verificationUrl) {
        
        String subject = "Verify Your Account";
        String template = "verification";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", name);
        templateData.put("verificationUrl", verificationUrl);
        
        UUID notificationId = eventNotificationService.sendTemplatedEmailNotification(
                userId, email, subject, template, templateData);
        
        // Build a simple response object
        NotificationDTO response = NotificationDTO.builder()
                .id(notificationId)
                .userId(userId)
                .subject(subject)
                .recipient(email)
                .type(NotificationType.USER_ACCOUNT_VERIFICATION)
                .channel("email")
                .sent(true)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/welcome")
    public ResponseEntity<NotificationDTO> sendWelcomeEmail(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String name) {
        
        String subject = "Welcome to EV SaaS Platform";
        String template = "welcome";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", name);
        
        UUID notificationId = eventNotificationService.sendTemplatedEmailNotification(
                userId, email, subject, template, templateData);
        
        // Build a simple response object
        NotificationDTO response = NotificationDTO.builder()
                .id(notificationId)
                .userId(userId)
                .subject(subject)
                .recipient(email)
                .type(NotificationType.USER_WELCOME)
                .channel("email")
                .sent(true)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/password-reset")
    public ResponseEntity<NotificationDTO> sendPasswordResetEmail(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String resetUrl) {
        
        String subject = "Reset Your Password";
        String template = "password-reset";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", name);
        templateData.put("resetUrl", resetUrl);
        
        UUID notificationId = eventNotificationService.sendTemplatedEmailNotification(
                userId, email, subject, template, templateData);
        
        // Build a simple response object
        NotificationDTO response = NotificationDTO.builder()
                .id(notificationId)
                .userId(userId)
                .subject(subject)
                .recipient(email)
                .type(NotificationType.USER_PASSWORD_RESET)
                .channel("email")
                .sent(true)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/session-started")
    public ResponseEntity<NotificationDTO> sendSessionStartedEmail(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String stationName,
            @RequestParam String startTime,
            @RequestParam String sessionId) {
        
        String subject = "Charging Session Started";
        String template = "session-started";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("stationName", stationName);
        templateData.put("startTime", startTime);
        templateData.put("sessionId", sessionId);
        
        UUID notificationId = eventNotificationService.sendTemplatedEmailNotification(
                userId, email, subject, template, templateData);
        
        // Build a simple response object
        NotificationDTO response = NotificationDTO.builder()
                .id(notificationId)
                .userId(userId)
                .subject(subject)
                .recipient(email)
                .type(NotificationType.CHARGING_STARTED)
                .channel("email")
                .sent(true)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/session-completed")
    public ResponseEntity<NotificationDTO> sendSessionCompletedEmail(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String stationName,
            @RequestParam String duration,
            @RequestParam String energy,
            @RequestParam String cost,
            @RequestParam String invoiceUrl) {
        
        String subject = "Charging Session Completed";
        String template = "session-completed";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("stationName", stationName);
        templateData.put("duration", duration);
        templateData.put("energy", energy);
        templateData.put("cost", cost);
        templateData.put("invoiceUrl", invoiceUrl);
        
        UUID notificationId = eventNotificationService.sendTemplatedEmailNotification(
                userId, email, subject, template, templateData);
        
        // Build a simple response object
        NotificationDTO response = NotificationDTO.builder()
                .id(notificationId)
                .userId(userId)
                .subject(subject)
                .recipient(email)
                .type(NotificationType.CHARGING_COMPLETED)
                .channel("email")
                .sent(true)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/invoice")
    public ResponseEntity<NotificationDTO> sendInvoiceEmail(
            @RequestParam UUID userId,
            @RequestParam String email,
            @RequestParam String invoiceNumber,
            @RequestParam String amount,
            @RequestParam String dueDate,
            @RequestParam String invoiceUrl) {
        
        String subject = "Your Invoice #" + invoiceNumber;
        String template = "invoice";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("invoiceNumber", invoiceNumber);
        templateData.put("amount", amount);
        templateData.put("dueDate", dueDate);
        templateData.put("invoiceUrl", invoiceUrl);
        
        UUID notificationId = eventNotificationService.sendTemplatedEmailNotification(
                userId, email, subject, template, templateData);
        
        // Build a simple response object
        NotificationDTO response = NotificationDTO.builder()
                .id(notificationId)
                .userId(userId)
                .subject(subject)
                .recipient(email)
                .type(NotificationType.INVOICE_CREATED)
                .channel("email")
                .sent(true)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
} 