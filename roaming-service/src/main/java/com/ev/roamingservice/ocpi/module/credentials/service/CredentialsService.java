package com.ev.roamingservice.ocpi.module.credentials.service;

import com.ev.roamingservice.ocpi.module.credentials.dto.Credentials;

/**
 * Service for handling OCPI credentials
 */
public interface CredentialsService {

    /**
     * Store credentials from an OCPI client
     * @param credentials the credentials to store
     */
    void storeCredentials(Credentials credentials);

    /**
     * Update credentials from an OCPI client
     * @param credentials the credentials to update
     */
    void updateCredentials(Credentials credentials);

    /**
     * Delete credentials for an OCPI client
     */
    void deleteCredentials();

    /**
     * Get credentials by token
     * @param token the token to look up
     * @return the credentials or null if not found
     */
    Credentials getCredentialsByToken(String token);

    /**
     * Validate a token from an OCPI client
     * @param token the token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
} 