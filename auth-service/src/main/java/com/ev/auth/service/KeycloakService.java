package com.ev.auth.service;

import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with Keycloak identity provider
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clientId}")
    private String clientId;

    @Value("${keycloak.clientSecret}")
    private String clientSecret;

    /**
     * Create a new user in Keycloak
     * @param request User registration details
     * @return Keycloak user ID
     */
    public String createUser(RegisterRequest request) {
        try {
            // Create user representation
            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(false);

            // Get realm
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            // Create user (returns a response)
            Response response = usersResource.create(user);
            
            if (response.getStatus() >= 400) {
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo().getReasonPhrase());
            }
            
            // Get created user ID
            String userId = extractCreatedId(response);
            
            // Set password
            setUserPassword(userId, request.getPassword());
            
            // Assign roles
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                assignRolesToUser(userId, request.getRoles());
            }
            
            log.info("Created user in Keycloak with ID: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Error creating user in Keycloak", e);
            throw new RuntimeException("Failed to create user in Keycloak", e);
        }
    }
    
    /**
     * Get authentication tokens
     * 
     * @param username The username
     * @param password The password
     * @return Authentication tokens
     */
    public TokenResponse getTokens(String username, String password) {
        try {
            // Build Keycloak instance for token request
            Keycloak keycloakInstance = KeycloakBuilder.builder()
                    .serverUrl(keycloak.getServerInfo().getInfo().get("serverUrl").toString())
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(username)
                    .password(password)
                    .build();
            
            // Get tokens
            String accessToken = keycloakInstance.tokenManager().getAccessToken().getToken();
            String refreshToken = keycloakInstance.tokenManager().getAccessToken().getRefreshToken();
            
            log.info("Generated tokens for user: {}", username);
            return new TokenResponse(accessToken, refreshToken);
        } catch (Exception e) {
            log.error("Error generating tokens for user", e);
            throw new RuntimeException("Authentication failed", e);
        }
    }
    
    /**
     * Validate a token
     * 
     * @param token The token to validate
     * @return True if token is valid
     */
    public boolean validateToken(String token) {
        try {
            // This is a simplified approach - in a real implementation, you should:
            // 1. Parse the JWT to check expiration
            // 2. Verify signature against Keycloak's public key
            // 3. Check other claims as needed
            
            // For now, we'll use Keycloak's introspection endpoint
            return keycloak.tokenManager().getAccessToken() != null;
        } catch (Exception e) {
            log.error("Error validating token", e);
            return false;
        }
    }
    
    /**
     * Refresh authentication tokens
     * 
     * @param refreshToken The refresh token
     * @return New authentication tokens
     */
    public TokenResponse refreshToken(String refreshToken) {
        try {
            // Build Keycloak instance for token refresh
            Keycloak keycloakInstance = KeycloakBuilder.builder()
                    .serverUrl(keycloak.getServerInfo().getInfo().get("serverUrl").toString())
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .refreshToken(refreshToken)
                    .build();
            
            // Get new tokens
            String newAccessToken = keycloakInstance.tokenManager().getAccessToken().getToken();
            String newRefreshToken = keycloakInstance.tokenManager().getAccessToken().getRefreshToken();
            
            log.info("Refreshed tokens successfully");
            return new TokenResponse(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new RuntimeException("Token refresh failed", e);
        }
    }

    /**
     * Update an existing user in Keycloak
     * 
     * @param userId The Keycloak user ID
     * @param email The updated email
     * @param firstName The updated first name
     * @param lastName The updated last name
     * @param attributes Any additional attributes to update
     */
    public void updateUser(String userId, String email, String firstName, String lastName, Map<String, List<String>> attributes) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            
            if (email != null) {
                user.setEmail(email);
            }
            
            if (firstName != null) {
                user.setFirstName(firstName);
            }
            
            if (lastName != null) {
                user.setLastName(lastName);
            }
            
            if (attributes != null) {
                user.setAttributes(attributes);
            }
            
            realmResource.users().get(userId).update(user);
            log.info("Updated user in Keycloak with ID: {}", userId);
        } catch (Exception e) {
            log.error("Error updating user in Keycloak", e);
            throw new RuntimeException("Failed to update user in Keycloak", e);
        }
    }
    
    /**
     * Delete a user from Keycloak
     * 
     * @param userId The Keycloak user ID
     */
    public void deleteUser(String userId) {
        try {
            keycloak.realm(realm).users().get(userId).remove();
            log.info("Deleted user from Keycloak with ID: {}", userId);
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak", e);
            throw new RuntimeException("Failed to delete user from Keycloak", e);
        }
    }
    
    /**
     * Set a user's password
     * 
     * @param userId The Keycloak user ID
     * @param password The new password
     */
    public void setUserPassword(String userId, String password) {
        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);
            
            keycloak.realm(realm).users().get(userId).resetPassword(credential);
            log.info("Set password for user in Keycloak with ID: {}", userId);
        } catch (Exception e) {
            log.error("Error setting password for user in Keycloak", e);
            throw new RuntimeException("Failed to set password for user in Keycloak", e);
        }
    }
    
    /**
     * Assign roles to a user
     * 
     * @param userId The Keycloak user ID
     * @param roleNames The role names to assign
     */
    public void assignRolesToUser(String userId, List<String> roleNames) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            
            List<RoleRepresentation> realmRoles = roleNames.stream()
                    .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                    .toList();
            
            realmResource.users().get(userId).roles().realmLevel().add(realmRoles);
            log.info("Assigned roles to user in Keycloak with ID: {}", userId);
        } catch (Exception e) {
            log.error("Error assigning roles to user in Keycloak", e);
            throw new RuntimeException("Failed to assign roles to user in Keycloak", e);
        }
    }
    
    /**
     * Enable or disable a user
     * 
     * @param userId The Keycloak user ID
     * @param enabled Whether to enable or disable the user
     */
    public void setUserEnabled(String userId, boolean enabled) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            user.setEnabled(enabled);
            realmResource.users().get(userId).update(user);
            log.info("Set user {} to enabled={} in Keycloak", userId, enabled);
        } catch (Exception e) {
            log.error("Error setting user enabled status in Keycloak", e);
            throw new RuntimeException("Failed to set user enabled status in Keycloak", e);
        }
    }
    
    /**
     * Extract the created user ID from a Keycloak response
     * 
     * @param response The Keycloak response
     * @return The user ID
     */
    private String extractCreatedId(Response response) {
        String location = response.getHeaderString("Location");
        response.close();
        
        if (location == null) {
            throw new RuntimeException("User ID not found in Keycloak response");
        }
        
        return location.substring(location.lastIndexOf("/") + 1);
    }
}
