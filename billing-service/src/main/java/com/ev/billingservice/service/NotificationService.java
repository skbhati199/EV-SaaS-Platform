package com.ev.billingservice.service;

import com.ev.billingservice.model.Invoice;
import com.ev.billingservice.model.Payment;
import com.ev.billingservice.model.Subscription;

public interface NotificationService {
    
    void sendInvoiceCreatedNotification(Invoice invoice);
    
    void sendPaymentReceivedNotification(Payment payment);
    
    void sendPaymentReminderNotification(Invoice invoice);
    
    void sendPaymentOverdueNotification(Invoice invoice);
    
    void sendSubscriptionCreatedNotification(Subscription subscription);
    
    void sendSubscriptionCanceledNotification(Subscription subscription);
} 