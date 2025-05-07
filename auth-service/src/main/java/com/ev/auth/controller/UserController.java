package com.ev.auth.controller;

import com.ev.auth.dto.UserResponse;
import com.ev.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User management operations")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final UserService userService;
    
    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = "Retrieves the profile of the currently authenticated user",
        tags = {"User Management"},
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully", 
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming the principal contains the user ID
        log.info("Fetching current user profile for ID: {}", userId);
        
        try {
            UUID userUuid = UUID.fromString(userId);
            Optional<UserResponse> userResponse = userService.getUserById(userUuid);
            return userResponse
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            log.error("Invalid user ID format: {}", userId, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users in the system (Admin only)",
        tags = {"User Management"},
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of users retrieved successfully", 
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Fetching all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.subject")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves user details by user ID (Admin or own account)",
        tags = {"User Management"},
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found", 
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        log.info("Fetching user with ID: {}", id);
        
        Optional<UserResponse> userResponse = userService.getUserById(id);
        return userResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get user by email",
        description = "Retrieves user details by email (Admin only)",
        tags = {"User Management"},
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found", 
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "User email", required = true, example = "user@example.com")
            @PathVariable String email) {
        log.info("Fetching user with email: {}", email);
        
        Optional<UserResponse> userResponse = userService.getUserByEmail(email);
        return userResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Deactivate user",
        description = "Deactivates a user account (Admin only)",
        tags = {"User Management"},
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deactivated successfully", 
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<UserResponse> deactivateUser(
            @Parameter(description = "User ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        log.info("Deactivating user with ID: {}", id);
        
        UserResponse userResponse = userService.deactivateUser(id);
        return ResponseEntity.ok(userResponse);
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Activate user",
        description = "Activates a deactivated user account (Admin only)",
        tags = {"User Management"},
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User activated successfully", 
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<UserResponse> activateUser(
            @Parameter(description = "User ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        log.info("Activating user with ID: {}", id);
        
        UserResponse userResponse = userService.activateUser(id);
        return ResponseEntity.ok(userResponse);
    }
}
