package com.ev.notificationservice.model;

/**
 * Enum representing different types of notifications
 */
public enum NotificationType {
    // User related notifications
    USER_WELCOME,
    USER_PASSWORD_RESET,
    USER_ACCOUNT_VERIFICATION,
    USER_PROFILE_UPDATE,
    
    // Charging related notifications
    CHARGING_STARTED,
    CHARGING_COMPLETED,
    CHARGING_INTERRUPTED,
    CHARGING_ERROR,
    
    // Payment related notifications
    PAYMENT_RECEIVED,
    PAYMENT_FAILED,
    PAYMENT_REFUNDED,
    PAYMENT_UPCOMING,
    
    // Invoice related notifications
    INVOICE_CREATED,
    INVOICE_PAID,
    INVOICE_OVERDUE,
    INVOICE_REMINDER,
    
    // Station related notifications
    STATION_OFFLINE,
    STATION_MAINTENANCE,
    STATION_AVAILABLE,
    
    // Subscription related notifications
    SUBSCRIPTION_CREATED,
    SUBSCRIPTION_RENEWAL,
    SUBSCRIPTION_EXPIRING,
    SUBSCRIPTION_EXPIRED,
    
    // System notifications
    SYSTEM_MAINTENANCE,
    SYSTEM_OUTAGE,
    SYSTEM_ANNOUNCEMENT
} 