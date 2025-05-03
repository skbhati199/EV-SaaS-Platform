package com.ev.roamingservice.ocpi.module.credentials.controller;

import com.ev.roamingservice.config.OcpiConfig;
import com.ev.roamingservice.dto.OcpiResponse;
import com.ev.roamingservice.ocpi.module.credentials.dto.Credentials;
import com.ev.roamingservice.ocpi.module.credentials.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

/**
 * Controller for handling OCPI credentials module
 */
@RestController
@RequestMapping("${ocpi.base-path}/2.2/credentials")
public class CredentialsController {

    private final OcpiConfig ocpiConfig;
    private final CredentialsService credentialsService;

    @Autowired
    public CredentialsController(OcpiConfig ocpiConfig, CredentialsService credentialsService) {
        this.ocpiConfig = ocpiConfig;
        this.credentialsService = credentialsService;
    }

    /**
     * Get credentials for the OCPI client
     * @return ResponseEntity with credentials
     */
    @GetMapping
    public ResponseEntity<OcpiResponse<Credentials>> getCredentials() {
        return ResponseEntity.ok(OcpiResponse.success(createCredentials()));
    }

    /**
     * Register credentials from an OCPI client
     * @param credentials the credentials from the client
     * @return ResponseEntity with our credentials
     */
    @PostMapping
    public ResponseEntity<OcpiResponse<Credentials>> registerCredentials(@RequestBody Credentials credentials) {
        // Store the given credentials
        credentialsService.storeCredentials(credentials);
        
        // Return our credentials
        return ResponseEntity.ok(OcpiResponse.success(createCredentials()));
    }

    /**
     * Update credentials from an OCPI client
     * @param credentials the credentials from the client
     * @return ResponseEntity with our credentials
     */
    @PutMapping
    public ResponseEntity<OcpiResponse<Credentials>> updateCredentials(@RequestBody Credentials credentials) {
        // Update the given credentials
        credentialsService.updateCredentials(credentials);
        
        // Return our credentials
        return ResponseEntity.ok(OcpiResponse.success(createCredentials()));
    }

    /**
     * Delete credentials for an OCPI client
     * @return ResponseEntity with success status
     */
    @DeleteMapping
    public ResponseEntity<OcpiResponse<String>> deleteCredentials() {
        // Delete credentials based on token in header
        credentialsService.deleteCredentials();
        
        return ResponseEntity.ok(OcpiResponse.success("Credentials deleted"));
    }

    /**
     * Create our credentials to send to the client
     * @return Credentials object
     */
    private Credentials createCredentials() {
        // In a real implementation, we would generate a new token and store it
        String token = UUID.randomUUID().toString();
        
        Credentials.BusinessDetails businessDetails = new Credentials.BusinessDetails(
                "EV SaaS Platform", 
                "https://ev-saas-platform.com", 
                new Credentials.Image("https://ev-saas-platform.com/logo.png", null, "OPERATOR", "png", 512, 512)
        );
        
        Credentials.Role role = new Credentials.Role(
                ocpiConfig.getRole(),
                businessDetails,
                ocpiConfig.getPartyId(),
                ocpiConfig.getCountryCode()
        );
        
        return new Credentials(
                token,
                ocpiConfig.getExternalUrl() + ocpiConfig.getBasePath(),
                businessDetails,
                ocpiConfig.getPartyId(),
                ocpiConfig.getCountryCode(),
                Collections.singletonList(role)
        );
    }
} 