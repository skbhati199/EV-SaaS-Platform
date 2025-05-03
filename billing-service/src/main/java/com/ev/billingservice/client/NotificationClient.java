package com.ev.billingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "notification-service", url = "${notification.service.url:http://localhost:8086}")
public interface NotificationClient {
    
    @PostMapping("/api/v1/notification-templates/invoice-created")
    ResponseEntity<?> sendInvoiceCreatedNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID invoiceId,
            @RequestParam String invoiceNumber,
            @RequestParam String amount,
            @RequestParam String dueDate);
    
    @PostMapping("/api/v1/notification-templates/payment-received")
    ResponseEntity<?> sendPaymentReceivedNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID paymentId,
            @RequestParam UUID invoiceId,
            @RequestParam String invoiceNumber,
            @RequestParam String amount,
            @RequestParam String paymentDate);
    
    @PostMapping("/api/v1/notification-templates/payment-reminder")
    ResponseEntity<?> sendPaymentReminderNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID invoiceId,
            @RequestParam String invoiceNumber,
            @RequestParam String amount,
            @RequestParam String dueDate);
    
    @PostMapping("/api/v1/notification-templates/payment-overdue")
    ResponseEntity<?> sendPaymentOverdueNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID invoiceId,
            @RequestParam String invoiceNumber,
            @RequestParam String amount,
            @RequestParam String dueDate);
    
    @PostMapping("/api/v1/notification-templates/subscription-created")
    ResponseEntity<?> sendSubscriptionCreatedNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID subscriptionId,
            @RequestParam String planName,
            @RequestParam String startDate,
            @RequestParam String price);
    
    @PostMapping("/api/v1/notification-templates/subscription-canceled")
    ResponseEntity<?> sendSubscriptionCanceledNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID subscriptionId,
            @RequestParam String planName,
            @RequestParam String endDate);
} 