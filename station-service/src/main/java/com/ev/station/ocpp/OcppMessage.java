package com.ev.station.ocpp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcppMessage {
    
    public enum MessageTypeId {
        CALL(2),
        CALLRESULT(3),
        CALLERROR(4);
        
        private final int value;
        
        MessageTypeId(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        public static MessageTypeId fromValue(int value) {
            for (MessageTypeId type : values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid MessageTypeId value: " + value);
        }
    }
    
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonDeserialize(using = OcppMessageDeserializer.class)
    private MessageTypeId messageTypeId;
    
    private String messageId;
    
    private String action;
    
    private Object payload;
    
    // Constructor for CALL messages
    public static OcppMessage createCallMessage(String messageId, String action, Object payload) {
        return OcppMessage.builder()
                .messageTypeId(MessageTypeId.CALL)
                .messageId(messageId)
                .action(action)
                .payload(payload)
                .build();
    }
    
    // Constructor for CALLRESULT messages
    public static OcppMessage createCallResultMessage(String messageId, Object payload) {
        return OcppMessage.builder()
                .messageTypeId(MessageTypeId.CALLRESULT)
                .messageId(messageId)
                .payload(payload)
                .build();
    }
    
    // Constructor for CALLERROR messages
    public static OcppMessage createCallErrorMessage(
            String messageId, String errorCode, String errorDescription, Object errorDetails) {
        return OcppMessage.builder()
                .messageTypeId(MessageTypeId.CALLERROR)
                .messageId(messageId)
                .action(errorCode)
                .payload(new Object[]{errorCode, errorDescription, errorDetails})
                .build();
    }
    
    /**
     * Convert OcppMessage to JSON string
     * @return JSON string representation of the message
     */
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting OCPP message to JSON", e);
        }
    }
    
    /**
     * Parse JSON node to OcppMessage using the provided ObjectMapper
     * @param node JsonNode to parse
     * @param mapper ObjectMapper to use for parsing
     * @return parsed OcppMessage
     */
    public static OcppMessage fromJson(JsonNode node, ObjectMapper mapper) {
        try {
            return mapper.treeToValue(node, OcppMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing OCPP message from JSON", e);
        }
    }
} 