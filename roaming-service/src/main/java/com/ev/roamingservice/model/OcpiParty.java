package com.ev.roamingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing an OCPI Party (CPO or EMSP)
 */
@Entity
@Table(name = "ocpi_parties")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcpiParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "party_id", nullable = false, length = 3)
    private String partyId;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;
    
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private OcpiRole role;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "versions_url")
    private String versionsUrl;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OcpiConnectionStatus status;

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