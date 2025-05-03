package com.ev.roamingservice.repository;

import com.ev.roamingservice.model.OcpiParty;
import com.ev.roamingservice.model.OcpiToken;
import com.ev.roamingservice.model.OcpiTokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for accessing OCPI Token data
 */
@Repository
public interface OcpiTokenRepository extends JpaRepository<OcpiToken, Long> {
    
    /**
     * Find a token by its value
     * 
     * @param token Token value
     * @return Optional containing the token if found
     */
    Optional<OcpiToken> findByToken(String token);
    
    /**
     * Find a token by party and token type
     * 
     * @param party OCPI party
     * @param tokenType Token type
     * @return Optional containing the token if found
     */
    Optional<OcpiToken> findByPartyAndTokenType(OcpiParty party, OcpiTokenType tokenType);
    
    /**
     * Find all tokens for a specific party
     * 
     * @param party OCPI party
     * @return List of tokens for the party
     */
    java.util.List<OcpiToken> findByParty(OcpiParty party);
    
    /**
     * Delete tokens by party
     * 
     * @param party OCPI party
     */
    void deleteByParty(OcpiParty party);
} 