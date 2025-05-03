package com.ev.roamingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Standard OCPI response object as per OCPI 2.2 specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OcpiResponse<T> {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("data")
    private T data;

    @JsonProperty("timestamp")
    private String timestamp;

    // Standard OCPI status codes
    public enum StatusCode {
        SUCCESS(1000),
        CLIENT_ERROR(2000),
        SERVER_ERROR(3000),
        HUB_ERROR(4000);

        private final int code;

        StatusCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    // Constructors
    public OcpiResponse() {
        this.timestamp = java.time.Instant.now().toString();
    }

    public OcpiResponse(int statusCode, String statusMessage, T data) {
        this();
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.data = data;
    }

    // Static factory methods for common responses
    public static <T> OcpiResponse<T> success(T data) {
        return new OcpiResponse<>(StatusCode.SUCCESS.getCode(), "Success", data);
    }

    public static <T> OcpiResponse<T> clientError(String message) {
        return new OcpiResponse<>(StatusCode.CLIENT_ERROR.getCode(), message, null);
    }

    public static <T> OcpiResponse<T> serverError(String message) {
        return new OcpiResponse<>(StatusCode.SERVER_ERROR.getCode(), message, null);
    }

    // Getters and setters
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
} 