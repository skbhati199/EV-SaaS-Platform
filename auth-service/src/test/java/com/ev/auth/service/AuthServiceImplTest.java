package com.ev.auth.service;

import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserResponse;
import com.ev.auth.exception.AuthenticationException;
import com.ev.auth.exception.ValidationException;
import com.ev.auth.model.Role;
import com.ev.auth.model.User;
import com.ev.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User mockUser;
    private TokenResponse mockTokenResponse;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123!");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setRole("USER");

        mockUser = User.builder()
                .id(1L)
                .username("test@example.com")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        mockTokenResponse = TokenResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();
    }

    @Test
    void testRegisterUserSuccess() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(keycloakService.createUser(any(RegisterRequest.class))).thenReturn("keycloak-user-id");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserResponse response = authService.registerUser(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertEquals("USER", response.getRole());
        assertTrue(response.isActive());

        verify(userRepository).findByUsername("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
        verify(keycloakService).createUser(registerRequest);
        verify(passwordEncoder).encode("Password123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUserWithExistingUsername() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            authService.registerUser(registerRequest);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).findByUsername("test@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(keycloakService, never()).createUser(any(RegisterRequest.class));
    }

    @Test
    void testRegisterUserWithExistingEmail() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            authService.registerUser(registerRequest);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).findByUsername("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(keycloakService, never()).createUser(any(RegisterRequest.class));
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(keycloakService.getTokens(anyString(), anyString())).thenReturn(mockTokenResponse);

        // Act
        TokenResponse response = authService.login("test@example.com", "Password123!");

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600, response.getExpiresIn());

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("Password123!", "encodedPassword");
        verify(keycloakService).getTokens("test@example.com", "Password123!");
    }

    @Test
    void testLoginWithInvalidCredentials() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.login("test@example.com", "WrongPassword");
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("WrongPassword", "encodedPassword");
        verify(keycloakService, never()).getTokens(anyString(), anyString());
    }

    @Test
    void testLoginWithDisabledAccount() {
        // Arrange
        User disabledUser = User.builder()
                .id(1L)
                .username("test@example.com")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(false)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(disabledUser));

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.login("test@example.com", "Password123!");
        });

        assertEquals("Account is disabled", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(keycloakService, never()).getTokens(anyString(), anyString());
    }

    @Test
    void testLoginWithNonExistentUser() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.login("nonexistent@example.com", "Password123!");
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(keycloakService, never()).getTokens(anyString(), anyString());
    }
} 