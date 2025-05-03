package com.ev.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String channel; // EMAIL, SMS, PUSH, WEBHOOK
    
    private String recipient; // email address, phone number, device token
    
    private boolean sent;
    
    private boolean read;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime readAt;
    
    private UUID relatedEntityId; // ID of the related entity (invoice, payment, etc.)
    
    private String relatedEntityType; // Type of the related entity (Invoice, Payment, etc.)
    
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
} 