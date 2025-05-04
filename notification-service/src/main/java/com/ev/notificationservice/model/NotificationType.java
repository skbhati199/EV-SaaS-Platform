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
    USER_CREATED,
    USER_DELETED,
    PROFILE_UPDATED,
    ACCOUNT_DISABLED,
    ACCOUNT_ENABLED,
    ACCOUNT_LOCKED,
    
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
    
    // Wallet related notifications
    WALLET_CREATED,
    WALLET_TOPPED_UP,
    WALLET_PAYMENT_FAILED,
    WALLET_LOW_BALANCE,
    
    // RFID token related notifications
    RFID_TOKEN_CREATED,
    RFID_TOKEN_ACTIVATED,
    RFID_TOKEN_DEACTIVATED,
    
    // Subscription related notifications
    SUBSCRIPTION_CREATED,
    SUBSCRIPTION_RENEWAL,
    SUBSCRIPTION_EXPIRING,
    SUBSCRIPTION_EXPIRED,
    
    // System notifications
    SYSTEM_MAINTENANCE,
    SYSTEM_OUTAGE,
    SYSTEM_ANNOUNCEMENT;
    
    /**
     * Priority levels for notifications
     */
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
} 