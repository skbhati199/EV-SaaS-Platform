package com.ev.billingservice.model;

/**
 * Enum representing the status of a charging transaction
 */
public enum TransactionStatus {
    /**
     * Transaction is in progress
     */
    PENDING,
    
    /**
     * Transaction has been completed successfully
     */
    COMPLETED,
    
    /**
     * Transaction was canceled
     */
    CANCELED,
    
    /**
     * Transaction failed
     */
    FAILED,
    
    /**
     * Transaction is being processed
     */
    PROCESSING,
    
    /**
     * Transaction has been invoiced
     */
    INVOICED
} 