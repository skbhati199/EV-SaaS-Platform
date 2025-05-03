package com.ev.roamingservice.repository;

import com.ev.roamingservice.model.OcpiParty;
import com.ev.roamingservice.model.OcpiRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for accessing OCPI Party data
 */
@Repository
public interface OcpiPartyRepository extends JpaRepository<OcpiParty, Long> {
    
    /**
     * Find a party by its party ID and country code
     * 
     * @param partyId Party ID (3-letter code)
     * @param countryCode Country code (2-letter code)
     * @return Optional containing the party if found
     */
    Optional<OcpiParty> findByPartyIdAndCountryCode(String partyId, String countryCode);
    
    /**
     * Find all parties with a specific role
     * 
     * @param role OCPI role
     * @return List of parties with the specified role
     */
    java.util.List<OcpiParty> findByRole(OcpiRole role);
} 