package com.ev.station.ocpp;

/**
 * Exception thrown during OCPP operations
 */
public class OcppException extends RuntimeException {
    
    private final String messageId;
    private final String errorCode;
    private final String errorDescription;
    
    public OcppException(String messageId, String errorCode, String errorDescription) {
        super(String.format("OCPP error: %s, %s, %s", messageId, errorCode, errorDescription));
        this.messageId = messageId;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getErrorDescription() {
        return errorDescription;
    }
} 