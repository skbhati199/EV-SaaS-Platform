package com.ev.station.model;

/**
 * Represents the current status of an EVSE (Electric Vehicle Supply Equipment)
 */
public enum EVSEStatus {
    AVAILABLE,    // EVSE is available for charging
    OCCUPIED,     // EVSE is currently being used for charging
    RESERVED,     // EVSE is reserved but not currently in use
    OFFLINE,      // EVSE is not connected to the network
    OUT_OF_ORDER, // EVSE is not operational due to a fault
    MAINTENANCE   // EVSE is undergoing maintenance
}
