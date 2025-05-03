package com.ev.roamingservice.repository;

import com.ev.roamingservice.model.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Location entities
 */
@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    
    /**
     * Find a location by its unique identifiers
     * @param countryCode Country code
     * @param partyId Party ID
     * @param locationId Location ID
     * @return Optional of LocationEntity
     */
    Optional<LocationEntity> findByCountryCodeAndPartyIdAndLocationId(
            String countryCode, String partyId, String locationId);
    
    /**
     * Find all locations for a given CPO
     * @param countryCode Country code
     * @param partyId Party ID
     * @return List of LocationEntity
     */
    List<LocationEntity> findByCountryCodeAndPartyId(String countryCode, String partyId);
    
    /**
     * Delete a location by its unique identifiers
     * @param countryCode Country code
     * @param partyId Party ID
     * @param locationId Location ID
     */
    void deleteByCountryCodeAndPartyIdAndLocationId(
            String countryCode, String partyId, String locationId);
} 