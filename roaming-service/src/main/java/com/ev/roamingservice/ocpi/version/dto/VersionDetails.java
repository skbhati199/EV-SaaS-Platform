package com.ev.roamingservice.ocpi.version.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * OCPI Version Details object as per OCPI 2.2 specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionDetails {

    @JsonProperty("version")
    private String version;

    @JsonProperty("endpoints")
    private List<Endpoint> endpoints;

    public VersionDetails() {
    }

    public VersionDetails(String version, List<Endpoint> endpoints) {
        this.version = version;
        this.endpoints = endpoints;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    /**
     * Endpoint object as per OCPI 2.2 specification
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Endpoint {

        @JsonProperty("identifier")
        private String identifier;

        @JsonProperty("role")
        private String role;

        @JsonProperty("url")
        private String url;

        public Endpoint() {
        }

        public Endpoint(String identifier, String role, String url) {
            this.identifier = identifier;
            this.role = role;
            this.url = url;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
} 