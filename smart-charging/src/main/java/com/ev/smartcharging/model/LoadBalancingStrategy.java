package com.ev.smartcharging.model;

public enum LoadBalancingStrategy {
    ROUND_ROBIN,      // Distribute power equally among all stations
    FIRST_COME_FIRST_SERVE, // Prioritize based on connection order
    PRIORITY_BASED,   // Prioritize based on user or vehicle priority
    DYNAMIC,          // Dynamically adjust based on real-time grid conditions
    TIME_OF_USE       // Adjust based on time-of-day electricity pricing
} 