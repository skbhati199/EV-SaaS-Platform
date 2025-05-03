package com.ev.roamingservice.model;

/**
 * Enum representing the types of tokens used in OCPI
 */
public enum OcpiTokenType {
    A,  // Token A - initial registration token
    B,  // Token B - temporary token used during credentials exchange
    C   // Token C - permanent token for ongoing API calls
} 