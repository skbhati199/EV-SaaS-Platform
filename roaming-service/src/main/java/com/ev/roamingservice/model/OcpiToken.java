package com.ev.roamingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing an OCPI Token used for authentication between parties
 */
@Entity
@Table(name = "ocpi_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcpiToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "token_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OcpiTokenType tokenType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private OcpiParty party;
    
    @Column(name = "valid_until")
    private LocalDateTime validUntil;
    
    @Column(name = "revoked")
    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 