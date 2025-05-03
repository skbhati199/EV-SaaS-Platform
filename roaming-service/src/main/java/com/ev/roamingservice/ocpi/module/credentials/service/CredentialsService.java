package com.ev.roamingservice.ocpi.module.credentials.service;

import com.ev.roamingservice.ocpi.module.credentials.dto.Credentials;

/**
 * Service for handling OCPI credentials
 */
public interface CredentialsService {

    /**
     * Store credentials for a party
     * @param credentials the credentials to store
     */
    void storeCredentials(Credentials credentials);

    /**
     * Update credentials for a party
     * @param credentials the updated credentials
     */
    void updateCredentials(Credentials credentials);

    /**
     * Delete credentials for the authenticated party
     */
    void deleteCredentials();

    /**
     * Get credentials by token
     * @param token the token to look up
     * @return the credentials if found, null otherwise
     */
    Credentials getCredentialsByToken(String token);

    /**
     * Validate a token
     * @param token the token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
} 