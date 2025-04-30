package com.ev.auth.controller;

import com.ev.auth.dto.UserResponse;
import com.ev.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Fetching all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.subject")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        log.info("Fetching user with ID: {}", id);
        
        Optional<UserResponse> userResponse = userService.getUserById(id);
        return userResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user with email: {}", email);
        
        Optional<UserResponse> userResponse = userService.getUserByEmail(email);
        return userResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable UUID id) {
        log.info("Deactivating user with ID: {}", id);
        
        UserResponse userResponse = userService.deactivateUser(id);
        return ResponseEntity.ok(userResponse);
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> activateUser(@PathVariable UUID id) {
        log.info("Activating user with ID: {}", id);
        
        UserResponse userResponse = userService.activateUser(id);
        return ResponseEntity.ok(userResponse);
    }
}
