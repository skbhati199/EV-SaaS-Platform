package com.ev.smartcharging.model;

public enum SessionStatus {
    PENDING,         // Registered but not yet started
    ACTIVE,          // Currently charging
    PAUSED,          // Paused by user or system
    POWER_REDUCED,   // Power reduced by smart charging system
    COMPLETED,       // Completed normally
    ERROR,           // Ended due to error
    TERMINATED       // Terminated by user or system
} 