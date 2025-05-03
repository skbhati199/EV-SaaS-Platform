package com.ev.roamingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard OCPI response format as per OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OcpiResponse<T> {
    
    @JsonProperty("status_code")
    private Integer statusCode;
    
    @JsonProperty("status_message")
    private String statusMessage;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("data")
    private T data;
    
    /**
     * Create a successful response
     * 
     * @param data The data to include in the response
     * @return The success response
     */
    public static <T> OcpiResponse<T> success(T data) {
        return OcpiResponse.<T>builder()
                .statusCode(1000)
                .statusMessage("Success")
                .timestamp(java.time.ZonedDateTime.now().toString())
                .data(data)
                .build();
    }
    
    /**
     * Create a generic error response
     * 
     * @param statusCode The error code
     * @param message The error message
     * @return The error response
     */
    public static <T> OcpiResponse<T> error(int statusCode, String message) {
        return OcpiResponse.<T>builder()
                .statusCode(statusCode)
                .statusMessage(message)
                .timestamp(java.time.ZonedDateTime.now().toString())
                .build();
    }
    
    /**
     * Create a 'not found' error response
     * 
     * @param message The error message
     * @return The not found response
     */
    public static <T> OcpiResponse<T> notFound(String message) {
        return error(2004, message);
    }
    
    /**
     * Create a 'client error' response
     * 
     * @param message The error message
     * @return The client error response
     */
    public static <T> OcpiResponse<T> clientError(String message) {
        return error(2001, message);
    }
    
    /**
     * Create a 'server error' response
     * 
     * @param message The error message
     * @return The server error response
     */
    public static <T> OcpiResponse<T> serverError(String message) {
        return error(3000, message);
    }
} 