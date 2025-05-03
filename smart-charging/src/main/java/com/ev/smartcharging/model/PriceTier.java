package com.ev.smartcharging.model;

public enum PriceTier {
    OFF_PEAK,      // Lowest electricity price
    SHOULDER,      // Medium electricity price
    PEAK,          // Highest electricity price
    DYNAMIC        // Price varies based on real-time grid conditions
} 