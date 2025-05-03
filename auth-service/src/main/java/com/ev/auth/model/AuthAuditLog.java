package com.ev.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auth_audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    private String details;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        id = UUID.randomUUID();
    }

    public enum EventType {
        LOGIN,
        LOGOUT,
        FAILED_LOGIN,
        PASSWORD_RESET,
        PASSWORD_CHANGE,
        TOKEN_REFRESH,
        TWO_FACTOR_SETUP,
        TWO_FACTOR_VERIFY,
        PASSWORDLESS_LOGIN_REQUEST,
        PASSWORDLESS_LOGIN,
        ACCOUNT_CREATE,
        ACCOUNT_UPDATE,
        ACCOUNT_DISABLE,
        ACCOUNT_ENABLE
    }

    public enum Status {
        SUCCESS,
        FAILURE,
        ATTEMPT,
        INFO
    }
} 