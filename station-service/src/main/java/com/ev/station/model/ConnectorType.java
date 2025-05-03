package com.ev.station.model;

/**
 * Represents the type of connector available on an EVSE
 */
public enum ConnectorType {
    TYPE_1,        // SAE J1772
    TYPE_2,        // IEC 62196 Type 2
    CCS1,          // Combined Charging System, Type 1
    CCS2,          // Combined Charging System, Type 2
    CHADEMO,       // CHAdeMO
    TESLA,         // Tesla connector
    GB_T,          // GB/T standard (China)
    NACS,          // North American Charging Standard (Tesla-derived)
    SCHUKO,        // Domestic connector
    UNKNOWN
}
