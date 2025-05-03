package com.ev.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    
    private UUID id;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotBlank(message = "Notification type is required")
    private String type;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    private String content;
    
    @NotBlank(message = "Channel is required")
    private String channel;
    
    @NotBlank(message = "Recipient is required")
    private String recipient;
    
    private boolean sent;
    
    private boolean read;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime readAt;
    
    private UUID relatedEntityId;
    
    private String relatedEntityType;
} 