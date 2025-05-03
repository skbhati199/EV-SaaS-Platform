package com.ev.roamingservice.ocpi.module.credentials.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for OCPI Credentials module
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Credentials {
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("roles")
    private List<CredentialsRole> roles;
    
    @JsonProperty("business_details")
    private BusinessDetails businessDetails;
    
    @JsonProperty("party_id")
    private String partyId;
    
    @JsonProperty("country_code")
    private String countryCode;
}

/**
 * OCPI Credentials object as per OCPI 2.2 specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public static class BusinessDetails {

    @JsonProperty("name")
    private String name;

    @JsonProperty("website")
    private String website;

    @JsonProperty("logo")
    private Image logo;

    public BusinessDetails() {
    }

    public BusinessDetails(String name, String website, Image logo) {
        this.name = name;
        this.website = website;
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Image getLogo() {
        return logo;
    }

    public void setLogo(Image logo) {
        this.logo = logo;
    }
}

/**
 * Image object as per OCPI 2.2 specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public static class Image {

    @JsonProperty("url")
    private String url;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("category")
    private String category;

    @JsonProperty("type")
    private String type;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    public Image() {
    }

    public Image(String url, String thumbnail, String category, String type, Integer width, Integer height) {
        this.url = url;
        this.thumbnail = thumbnail;
        this.category = category;
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}

/**
 * Role object as per OCPI 2.2 specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public static class Role {

    @JsonProperty("role")
    private String role;

    @JsonProperty("business_details")
    private BusinessDetails businessDetails;

    @JsonProperty("party_id")
    private String partyId;

    @JsonProperty("country_code")
    private String countryCode;

    public Role() {
    }

    public Role(String role, BusinessDetails businessDetails, String partyId, String countryCode) {
        this.role = role;
        this.businessDetails = businessDetails;
        this.partyId = partyId;
        this.countryCode = countryCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BusinessDetails getBusinessDetails() {
        return businessDetails;
    }

    public void setBusinessDetails(BusinessDetails businessDetails) {
        this.businessDetails = businessDetails;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
} 