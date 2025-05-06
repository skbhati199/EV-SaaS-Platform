package com.ev.auth.service;

import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Use KeycloakAdminService instead
 */
@Deprecated
@Service("legacyKeycloakService")
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.resource}")
    private String clientId;
    
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    
    @Value("${keycloak.admin-username}")
    private String adminUsername;
    
    @Value("${keycloak.admin-password}")
    private String adminPassword;
    
    @Override
    public String createUser(RegisterRequest request) {
        Keycloak keycloakAdmin = getKeycloakAdminClient();
        RealmResource realmResource = keycloakAdmin.realm(realm);
        UsersResource usersResource = realmResource.users();
        
        // Create user representation
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmailVerified(true);
        
        // Create user in Keycloak
        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            log.error("Failed to create user in Keycloak: {}", response.getStatusInfo().getReasonPhrase());
            throw new AuthenticationException("Failed to create user in Keycloak");
        }
        
        // Get user ID from response
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        
        // Set user password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        usersResource.get(userId).resetPassword(credential);
        
        // Assign role to user if specified
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            RoleRepresentation roleRepresentation = realmResource.roles().get(request.getRole()).toRepresentation();
            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        }
        
        log.info("User created in Keycloak with ID: {}", userId);
        return userId;
    }
    
    @Override
    public TokenResponse getTokens(String username, String password) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(username)
                    .password(password)
                    .build();
            
            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
            
            return TokenResponse.builder()
                    .accessToken(tokenResponse.getToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .tokenType(tokenResponse.getTokenType())
                    .expiresIn(tokenResponse.getExpiresIn())
                    .build();
        } catch (Exception e) {
            log.error("Failed to authenticate user: {}", e.getMessage());
            throw new AuthenticationException("Invalid credentials");
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            // For token validation in a real implementation, you would use Keycloak's token
            // introspection endpoint. This is a simplified implementation for demonstration.
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
            
            // In a real implementation, you would call the introspection endpoint
            // This is simplified for demonstration purposes
            return token != null && !token.isEmpty();
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public TokenResponse refreshToken(String refreshToken) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType("refresh_token")
                    .build();
            
            // In a real implementation, you would use the refresh token
            // to get a new access token from Keycloak
            // This is simplified for demonstration purposes
            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
            
            return TokenResponse.builder()
                    .accessToken(tokenResponse.getToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .tokenType(tokenResponse.getTokenType())
                    .expiresIn(tokenResponse.getExpiresIn())
                    .build();
        } catch (Exception e) {
            log.error("Failed to refresh token: {}", e.getMessage());
            throw new AuthenticationException("Invalid refresh token");
        }
    }
    
    @Override
    public void updateUser(String userId, String email, String firstName, String lastName, Map<String, List<String>> attributes) {
        Keycloak keycloakAdmin = getKeycloakAdminClient();
        RealmResource realmResource = keycloakAdmin.realm(realm);
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
        log.info("User updated in Keycloak with ID: {}", userId);
    }
    
    @Override
    public void deleteUser(String userId) {
        Keycloak keycloakAdmin = getKeycloakAdminClient();
        keycloakAdmin.realm(realm).users().get(userId).remove();
        log.info("User deleted from Keycloak with ID: {}", userId);
    }
    
    public void setUserPassword(String userId, String password) {
        Keycloak keycloakAdmin = getKeycloakAdminClient();
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        keycloakAdmin.realm(realm).users().get(userId).resetPassword(credential);
        log.info("Password set for user in Keycloak with ID: {}", userId);
    }
    
    public void assignRolesToUser(String userId, List<String> roleNames) {
        Keycloak keycloakAdmin = getKeycloakAdminClient();
        RealmResource realmResource = keycloakAdmin.realm(realm);
        
        List<RoleRepresentation> roles = roleNames.stream()
                .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                .toList();
        
        realmResource.users().get(userId).roles().realmLevel().add(roles);
        log.info("Roles assigned to user in Keycloak with ID: {}", userId);
    }
    
    public void setUserEnabled(String userId, boolean enabled) {
        Keycloak keycloakAdmin = getKeycloakAdminClient();
        UserRepresentation user = keycloakAdmin.realm(realm).users().get(userId).toRepresentation();
        user.setEnabled(enabled);
        keycloakAdmin.realm(realm).users().get(userId).update(user);
        log.info("User enabled state set to {} for ID: {}", enabled, userId);
    }
    
    private Keycloak getKeycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm("master")
                .clientId("admin-cli")
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }
}
