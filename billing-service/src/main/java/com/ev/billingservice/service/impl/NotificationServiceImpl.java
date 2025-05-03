package com.ev.billingservice.service.impl;

import com.ev.billingservice.client.NotificationClient;
import com.ev.billingservice.model.Invoice;
import com.ev.billingservice.model.Payment;
import com.ev.billingservice.model.Subscription;
import com.ev.billingservice.service.NotificationService;
import com.ev.billingservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationClient notificationClient;
    private final UserService userService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public void sendInvoiceCreatedNotification(Invoice invoice) {
        try {
            String recipientEmail = userService.getUserEmailById(invoice.getUserId());
            if (recipientEmail == null || recipientEmail.isEmpty()) {
                log.warn("Cannot send invoice created notification - no email for user ID: {}", invoice.getUserId());
                return;
            }
            
            notificationClient.sendInvoiceCreatedNotification(
                    invoice.getUserId(),
                    recipientEmail,
                    invoice.getId(),
                    invoice.getInvoiceNumber(),
                    invoice.getTotalAmount().toString(),
                    invoice.getDueDate().format(DATE_FORMATTER)
            );
            
            log.info("Invoice created notification sent for invoice ID: {}", invoice.getId());
        } catch (Exception e) {
            log.error("Failed to send invoice created notification for invoice ID: {}", invoice.getId(), e);
        }
    }
    
    @Override
    public void sendPaymentReceivedNotification(Payment payment) {
        try {
            String recipientEmail = userService.getUserEmailById(payment.getUserId());
            if (recipientEmail == null || recipientEmail.isEmpty()) {
                log.warn("Cannot send payment received notification - no email for user ID: {}", payment.getUserId());
                return;
            }
            
            Invoice invoice = payment.getInvoice();
            
            notificationClient.sendPaymentReceivedNotification(
                    payment.getUserId(),
                    recipientEmail,
                    payment.getId(),
                    invoice.getId(),
                    invoice.getInvoiceNumber(),
                    payment.getAmount().toString(),
                    payment.getPaymentDate().format(DATE_FORMATTER)
            );
            
            log.info("Payment received notification sent for payment ID: {}", payment.getId());
        } catch (Exception e) {
            log.error("Failed to send payment received notification for payment ID: {}", payment.getId(), e);
        }
    }
    
    @Override
    public void sendPaymentReminderNotification(Invoice invoice) {
        try {
            String recipientEmail = userService.getUserEmailById(invoice.getUserId());
            if (recipientEmail == null || recipientEmail.isEmpty()) {
                log.warn("Cannot send payment reminder notification - no email for user ID: {}", invoice.getUserId());
                return;
            }
            
            notificationClient.sendPaymentReminderNotification(
                    invoice.getUserId(),
                    recipientEmail,
                    invoice.getId(),
                    invoice.getInvoiceNumber(),
                    invoice.getTotalAmount().toString(),
                    invoice.getDueDate().format(DATE_FORMATTER)
            );
            
            log.info("Payment reminder notification sent for invoice ID: {}", invoice.getId());
        } catch (Exception e) {
            log.error("Failed to send payment reminder notification for invoice ID: {}", invoice.getId(), e);
        }
    }
    
    @Override
    public void sendPaymentOverdueNotification(Invoice invoice) {
        try {
            String recipientEmail = userService.getUserEmailById(invoice.getUserId());
            if (recipientEmail == null || recipientEmail.isEmpty()) {
                log.warn("Cannot send payment overdue notification - no email for user ID: {}", invoice.getUserId());
                return;
            }
            
            notificationClient.sendPaymentOverdueNotification(
                    invoice.getUserId(),
                    recipientEmail,
                    invoice.getId(),
                    invoice.getInvoiceNumber(),
                    invoice.getTotalAmount().toString(),
                    invoice.getDueDate().format(DATE_FORMATTER)
            );
            
            log.info("Payment overdue notification sent for invoice ID: {}", invoice.getId());
        } catch (Exception e) {
            log.error("Failed to send payment overdue notification for invoice ID: {}", invoice.getId(), e);
        }
    }
    
    @Override
    public void sendSubscriptionCreatedNotification(Subscription subscription) {
        try {
            String recipientEmail = userService.getUserEmailById(subscription.getUserId());
            if (recipientEmail == null || recipientEmail.isEmpty()) {
                log.warn("Cannot send subscription created notification - no email for user ID: {}", subscription.getUserId());
                return;
            }
            
            notificationClient.sendSubscriptionCreatedNotification(
                    subscription.getUserId(),
                    recipientEmail,
                    subscription.getId(),
                    subscription.getBillingPlan().getName(),
                    subscription.getStartDate().format(DATE_FORMATTER),
                    subscription.getPrice().toString()
            );
            
            log.info("Subscription created notification sent for subscription ID: {}", subscription.getId());
        } catch (Exception e) {
            log.error("Failed to send subscription created notification for subscription ID: {}", subscription.getId(), e);
        }
    }
    
    @Override
    public void sendSubscriptionCanceledNotification(Subscription subscription) {
        try {
            String recipientEmail = userService.getUserEmailById(subscription.getUserId());
            if (recipientEmail == null || recipientEmail.isEmpty()) {
                log.warn("Cannot send subscription canceled notification - no email for user ID: {}", subscription.getUserId());
                return;
            }
            
            notificationClient.sendSubscriptionCanceledNotification(
                    subscription.getUserId(),
                    recipientEmail,
                    subscription.getId(),
                    subscription.getBillingPlan().getName(),
                    subscription.getEndDate().format(DATE_FORMATTER)
            );
            
            log.info("Subscription canceled notification sent for subscription ID: {}", subscription.getId());
        } catch (Exception e) {
            log.error("Failed to send subscription canceled notification for subscription ID: {}", subscription.getId(), e);
        }
    }
} 