package com.ev.auth.controller;

import com.ev.auth.dto.LoginRequest;
import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserResponse;
import com.ev.auth.exception.UserConflictException;
import com.ev.auth.service.AuthService;
import com.ev.auth.service.KeycloakService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private KeycloakService keycloakService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserResponse userResponse;
    private TokenResponse tokenResponse;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123!");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setRole("USER");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Password123!");

        userResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role("USER")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        tokenResponse = TokenResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();
    }

    @Test
    void testRegisterSuccess() {
        // Arrange
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(userResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> requestEntity = new HttpEntity<>(registerRequest, headers);

        // Act
        ResponseEntity<UserResponse> responseEntity = restTemplate.exchange(
                "/api/v1/auth/register",
                HttpMethod.POST,
                requestEntity,
                UserResponse.class
        );

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("test@example.com", responseEntity.getBody().getEmail());
        assertEquals("Test", responseEntity.getBody().getFirstName());
        assertEquals("User", responseEntity.getBody().getLastName());
        assertEquals("USER", responseEntity.getBody().getRole());
        assertTrue(responseEntity.getBody().isActive());
    }

    @Test
    void testRegisterUserConflict() {
        // Arrange
        when(authService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new UserConflictException("User already exists with the provided username or email"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> requestEntity = new HttpEntity<>(registerRequest, headers);

        // Act
        ResponseEntity<Object> responseEntity = restTemplate.exchange(
                "/api/v1/auth/register",
                HttpMethod.POST,
                requestEntity,
                Object.class
        );

        // Assert
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        when(authService.login(anyString(), anyString())).thenReturn(tokenResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(loginRequest, headers);

        // Act
        ResponseEntity<TokenResponse> responseEntity = restTemplate.exchange(
                "/api/v1/auth/login",
                HttpMethod.POST,
                requestEntity,
                TokenResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("access-token", responseEntity.getBody().getAccessToken());
        assertEquals("refresh-token", responseEntity.getBody().getRefreshToken());
        assertEquals("Bearer", responseEntity.getBody().getTokenType());
        assertEquals(3600, responseEntity.getBody().getExpiresIn());
    }

    @Test
    void testValidateTokenTrue() {
        // Arrange
        when(keycloakService.validateToken(anyString())).thenReturn(true);
        
        // Act
        ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(
                "/api/v1/auth/validate?token=valid-token",
                Boolean.class
        );

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody());
    }

    @Test
    void testValidateTokenFalse() {
        // Arrange
        when(keycloakService.validateToken(anyString())).thenReturn(false);
        
        // Act
        ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(
                "/api/v1/auth/validate?token=invalid-token",
                Boolean.class
        );

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody());
    }
} 