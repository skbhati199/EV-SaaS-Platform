package com.ev.station.model;

/**
 * Represents the type of connector available on an EVSE
 */
public enum ConnectorType {
    TYPE_1,       // SAE J1772 (Type 1)
    TYPE_2,       // IEC 62196 (Type 2, Mennekes)
    CCS_1,        // Combined Charging System (Type 1)
    CCS_2,        // Combined Charging System (Type 2)
    CHADEMO,      // CHAdeMO DC fast charging
    TESLA,        // Tesla Supercharger
    GB_T,         // Chinese standard
    OTHER         // Other connector types
}
