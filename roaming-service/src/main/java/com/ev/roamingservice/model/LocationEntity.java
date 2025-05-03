package com.ev.roamingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Database entity representing an OCPI location
 */
@Entity
@Table(name = "ocpi_location")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;
    
    @Column(name = "party_id_text", nullable = false, length = 3)
    private String partyId;
    
    @Column(name = "location_id", nullable = false, length = 36)
    private String locationId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "city", nullable = false)
    private String city;
    
    @Column(name = "postal_code", length = 10)
    private String postalCode;
    
    @Column(name = "country", nullable = false)
    private String country;
    
    @Column(name = "coordinates", nullable = false, length = 50)
    private String coordinates;
    
    @Column(name = "time_zone")
    private String timeZone;
    
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvseEntity> evses;
    
    @Column(name = "last_updated")
    private ZonedDateTime lastUpdated;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
} 