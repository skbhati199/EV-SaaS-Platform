package com.ev.notificationservice.controller;

import com.ev.notificationservice.dto.NotificationDTO;
import com.ev.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notification-templates")
@RequiredArgsConstructor
public class NotificationTemplateController {
    
    private final NotificationService notificationService;
    
    @PostMapping("/invoice-created")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<NotificationDTO> sendInvoiceCreatedNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID invoiceId,
            @RequestParam String invoiceNumber,
            @RequestParam String amount,
            @RequestParam String dueDate) {
        
        String subject = "New Invoice: " + invoiceNumber;
        String content = String.format(
                "A new invoice has been created for your account.\n\n" +
                "Invoice Number: %s\n" +
                "Amount: %s\n" +
                "Due Date: %s\n\n" +
                "Please log in to your account to view and pay this invoice.",
                invoiceNumber, amount, dueDate);
        
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .type("INVOICE_CREATED")
                .subject(subject)
                .content(content)
                .channel("EMAIL")
                .recipient(recipient)
                .relatedEntityId(invoiceId)
                .relatedEntityType("INVOICE")
                .build();
        
        return new ResponseEntity<>(notificationService.createNotification(notificationDTO), HttpStatus.CREATED);
    }
    
    @PostMapping("/payment-received")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<NotificationDTO> sendPaymentReceivedNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID paymentId,
            @RequestParam UUID invoiceId,
            @RequestParam String invoiceNumber,
            @RequestParam String amount,
            @RequestParam String paymentDate) {
        
        String subject = "Payment Received: Invoice " + invoiceNumber;
        String content = String.format(
                "We have received your payment of %s for invoice %s on %s.\n\n" +
                "Thank you for your business!",
                amount, invoiceNumber, paymentDate);
        
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .type("PAYMENT_RECEIVED")
                .subject(subject)
                .content(content)
                .channel("EMAIL")
                .recipient(recipient)
                .relatedEntityId(paymentId)
                .relatedEntityType("PAYMENT")
                .build();
        
        return new ResponseEntity<>(notificationService.createNotification(notificationDTO), HttpStatus.CREATED);
    }
    
    @PostMapping("/payment-reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<NotificationDTO> sendPaymentReminderNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID invoiceId,
            @RequestParam String invoiceNumber,
            @RequestParam String amount,
            @RequestParam String dueDate) {
        
        String subject = "Payment Reminder: Invoice " + invoiceNumber;
        String content = String.format(
                "This is a reminder that invoice %s for %s is due on %s.\n\n" +
                "Please log in to your account to make a payment.",
                invoiceNumber, amount, dueDate);
        
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .type("PAYMENT_REMINDER")
                .subject(subject)
                .content(content)
                .channel("EMAIL")
                .recipient(recipient)
                .relatedEntityId(invoiceId)
                .relatedEntityType("INVOICE")
                .build();
        
        return new ResponseEntity<>(notificationService.createNotification(notificationDTO), HttpStatus.CREATED);
    }
    
    @PostMapping("/payment-overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<NotificationDTO> sendPaymentOverdueNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID invoiceId,
            @RequestParam String invoiceNumber,
            @RequestParam String amount,
            @RequestParam String dueDate) {
        
        String subject = "Payment Overdue: Invoice " + invoiceNumber;
        String content = String.format(
                "Invoice %s for %s was due on %s and is now overdue.\n\n" +
                "Please log in to your account to make a payment as soon as possible to avoid late fees.",
                invoiceNumber, amount, dueDate);
        
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .type("PAYMENT_OVERDUE")
                .subject(subject)
                .content(content)
                .channel("EMAIL")
                .recipient(recipient)
                .relatedEntityId(invoiceId)
                .relatedEntityType("INVOICE")
                .build();
        
        return new ResponseEntity<>(notificationService.createNotification(notificationDTO), HttpStatus.CREATED);
    }
    
    @PostMapping("/subscription-created")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<NotificationDTO> sendSubscriptionCreatedNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID subscriptionId,
            @RequestParam String planName,
            @RequestParam String startDate,
            @RequestParam String price) {
        
        String subject = "New Subscription: " + planName;
        String content = String.format(
                "Your subscription to %s has been activated.\n\n" +
                "Start Date: %s\n" +
                "Price: %s\n\n" +
                "Thank you for your subscription!",
                planName, startDate, price);
        
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .type("SUBSCRIPTION_CREATED")
                .subject(subject)
                .content(content)
                .channel("EMAIL")
                .recipient(recipient)
                .relatedEntityId(subscriptionId)
                .relatedEntityType("SUBSCRIPTION")
                .build();
        
        return new ResponseEntity<>(notificationService.createNotification(notificationDTO), HttpStatus.CREATED);
    }
    
    @PostMapping("/subscription-canceled")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<NotificationDTO> sendSubscriptionCanceledNotification(
            @RequestParam UUID userId,
            @RequestParam String recipient,
            @RequestParam UUID subscriptionId,
            @RequestParam String planName,
            @RequestParam String endDate) {
        
        String subject = "Subscription Canceled: " + planName;
        String content = String.format(
                "Your subscription to %s has been canceled.\n\n" +
                "End Date: %s\n\n" +
                "We're sorry to see you go. Please let us know if there's anything we can do to improve our service.",
                planName, endDate);
        
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .type("SUBSCRIPTION_CANCELED")
                .subject(subject)
                .content(content)
                .channel("EMAIL")
                .recipient(recipient)
                .relatedEntityId(subscriptionId)
                .relatedEntityType("SUBSCRIPTION")
                .build();
        
        return new ResponseEntity<>(notificationService.createNotification(notificationDTO), HttpStatus.CREATED);
    }
} 