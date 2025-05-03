package com.ev.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "passwordless_auth")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordlessAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    private boolean used;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        id = UUID.randomUUID();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(validUntil);
    }

    public boolean isValid() {
        return !isExpired() && !used;
    }
} 