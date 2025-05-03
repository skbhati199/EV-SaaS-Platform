package com.ev.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information response")
public class UserResponse {
    
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "User email address", example = "user@example.com")
    private String email;
    
    @Schema(description = "User first name", example = "John")
    private String firstName;
    
    @Schema(description = "User last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "User role", example = "ADMIN")
    private String role;
    
    @Schema(description = "Whether user account is active", example = "true")
    private boolean active;
    
    @Schema(description = "Timestamp when user was created", example = "2023-01-01T12:00:00")
    private LocalDateTime createdAt;
}
