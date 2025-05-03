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

@Service
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
        
        // Assign role to user
        RoleRepresentation roleRepresentation = realmResource.roles().get(request.getRole()).toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        
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
