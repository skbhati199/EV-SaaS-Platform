package com.ev.roamingservice.model;

/**
 * Enum representing the possible connection statuses in OCPI
 */
public enum OcpiConnectionStatus {
    PENDING,     // Connection is pending
    CONNECTED,   // Connection is established
    SUSPENDED,   // Connection is temporarily suspended
    DISCONNECTED // Connection is disconnected
} 