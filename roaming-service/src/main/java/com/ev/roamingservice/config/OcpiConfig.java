package com.ev.roamingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for OCPI-related properties
 */
@Configuration
public class OcpiConfig implements WebMvcConfigurer {

    @Value("${ocpi.version}")
    private String version;

    @Value("${ocpi.party.id}")
    private String partyId;

    @Value("${ocpi.country.code}")
    private String countryCode;

    @Value("${ocpi.base-path}")
    private String basePath;

    @Value("${ocpi.external-url}")
    private String externalUrl;

    @Value("${ocpi.role}")
    private String role;

    public String getVersion() {
        return version;
    }

    public String getPartyId() {
        return partyId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public String getRole() {
        return role;
    }

    /**
     * Constructs the full external URL for a given endpoint path
     * @param endpointPath the endpoint path to append
     * @return the full URL
     */
    public String getFullUrl(String endpointPath) {
        return externalUrl + basePath + endpointPath;
    }
} 