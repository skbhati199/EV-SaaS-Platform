package com.ev.auth.service;

import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.exception.UserConflictException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class KeycloakAdminServiceTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private Keycloak adminKeycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private Response response;

    @InjectMocks
    private KeycloakAdminService keycloakAdminService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setup() {
        // Set up private fields with ReflectionTestUtils
        ReflectionTestUtils.setField(keycloakAdminService, "realm", "ev-platform");
        ReflectionTestUtils.setField(keycloakAdminService, "clientId", "auth-service");
        ReflectionTestUtils.setField(keycloakAdminService, "clientSecret", "secret");
        ReflectionTestUtils.setField(keycloakAdminService, "authServerUrl", "http://localhost:8090/auth");
        ReflectionTestUtils.setField(keycloakAdminService, "adminUsername", "admin");
        ReflectionTestUtils.setField(keycloakAdminService, "adminPassword", "admin");

        // Set up request object
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123!");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setRole("USER");
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        // Arrange
        mockKeycloakAdminClient();
        
        // Mock search results (empty means no existing users)
        when(usersResource.search(anyString(), anyBoolean())).thenReturn(Collections.emptyList());
        when(usersResource.search(anyString(), anyInt(), anyInt())).thenReturn(Collections.emptyList());
        
        // Mock successful user creation
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.getLocation()).thenReturn(new URI("http://localhost:8090/auth/admin/realms/ev-platform/users/user-id-123"));

        // Act
        String userId = keycloakAdminService.createUser(registerRequest);

        // Assert
        assertNotNull(userId);
        assertEquals("user-id-123", userId);
        
        verify(usersResource).search(registerRequest.getUsername(), true);
        verify(usersResource).search(registerRequest.getEmail(), 0, 1);
        verify(usersResource).create(any(UserRepresentation.class));
    }

    @Test
    void testCreateUserWithExistingUsername() {
        // Arrange
        mockKeycloakAdminClient();
        
        // Mock search results with existing user by username
        List<UserRepresentation> existingUsers = new ArrayList<>();
        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setUsername("testuser");
        existingUsers.add(existingUser);
        
        when(usersResource.search(eq(registerRequest.getUsername()), anyBoolean())).thenReturn(existingUsers);

        // Act & Assert
        UserConflictException exception = assertThrows(UserConflictException.class, () -> {
            keycloakAdminService.createUser(registerRequest);
        });

        assertEquals("User with this username already exists", exception.getMessage());
        verify(usersResource).search(registerRequest.getUsername(), true);
        verify(usersResource, never()).create(any(UserRepresentation.class));
    }

    @Test
    void testCreateUserWithExistingEmail() {
        // Arrange
        mockKeycloakAdminClient();
        
        // Mock empty username search
        when(usersResource.search(eq(registerRequest.getUsername()), anyBoolean())).thenReturn(Collections.emptyList());
        
        // Mock existing email search
        List<UserRepresentation> existingUsersByEmail = new ArrayList<>();
        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setEmail("test@example.com");
        existingUsersByEmail.add(existingUser);
        
        when(usersResource.search(eq(registerRequest.getEmail()), anyInt(), anyInt())).thenReturn(existingUsersByEmail);

        // Act & Assert
        UserConflictException exception = assertThrows(UserConflictException.class, () -> {
            keycloakAdminService.createUser(registerRequest);
        });

        assertEquals("User with this email already exists", exception.getMessage());
        verify(usersResource).search(registerRequest.getUsername(), true);
        verify(usersResource).search(registerRequest.getEmail(), 0, 1);
        verify(usersResource, never()).create(any(UserRepresentation.class));
    }

    @Test
    void testCreateUserWithConflictResponse() {
        // Arrange
        mockKeycloakAdminClient();
        
        // Mock search results (empty means no existing users)
        when(usersResource.search(anyString(), anyBoolean())).thenReturn(Collections.emptyList());
        when(usersResource.search(anyString(), anyInt(), anyInt())).thenReturn(Collections.emptyList());
        
        // Mock conflict response (409)
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(409);
        when(response.getStatusInfo().getReasonPhrase()).thenReturn("Conflict");

        // Act & Assert
        UserConflictException exception = assertThrows(UserConflictException.class, () -> {
            keycloakAdminService.createUser(registerRequest);
        });

        assertEquals("User already exists in Keycloak: username or email is already taken", exception.getMessage());
        
        verify(usersResource).search(registerRequest.getUsername(), true);
        verify(usersResource).search(registerRequest.getEmail(), 0, 1);
        verify(usersResource).create(any(UserRepresentation.class));
    }

    @Test
    void testCreateUserWithGenericError() {
        // Arrange
        mockKeycloakAdminClient();
        
        // Mock search results (empty means no existing users)
        when(usersResource.search(anyString(), anyBoolean())).thenReturn(Collections.emptyList());
        when(usersResource.search(anyString(), anyInt(), anyInt())).thenReturn(Collections.emptyList());
        
        // Mock error response (500)
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(500);
        when(response.getStatusInfo().getReasonPhrase()).thenReturn("Internal Server Error");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            keycloakAdminService.createUser(registerRequest);
        });

        assertEquals("Failed to create user in Keycloak: Internal Server Error", exception.getMessage());
        
        verify(usersResource).search(registerRequest.getUsername(), true);
        verify(usersResource).search(registerRequest.getEmail(), 0, 1);
        verify(usersResource).create(any(UserRepresentation.class));
    }

    private void mockKeycloakAdminClient() {
        // Mock admin Keycloak client and associated resources
        try (MockedStatic<org.keycloak.admin.client.KeycloakBuilder> mockedBuilder = mockStatic(org.keycloak.admin.client.KeycloakBuilder.class)) {
            org.keycloak.admin.client.KeycloakBuilder builderMock = mock(org.keycloak.admin.client.KeycloakBuilder.class);
            
            mockedBuilder.when(org.keycloak.admin.client.KeycloakBuilder::builder).thenReturn(builderMock);
            
            when(builderMock.serverUrl(anyString())).thenReturn(builderMock);
            when(builderMock.realm(anyString())).thenReturn(builderMock);
            when(builderMock.username(anyString())).thenReturn(builderMock);
            when(builderMock.password(anyString())).thenReturn(builderMock);
            when(builderMock.clientId(anyString())).thenReturn(builderMock);
            when(builderMock.clientSecret(anyString())).thenReturn(builderMock);
            when(builderMock.build()).thenReturn(adminKeycloak);
            
            when(adminKeycloak.realm(anyString())).thenReturn(realmResource);
            when(realmResource.users()).thenReturn(usersResource);
        }
    }
} 