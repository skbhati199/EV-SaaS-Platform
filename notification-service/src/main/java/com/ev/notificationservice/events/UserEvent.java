package com.ev.notificationservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String id;
    private String userId;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private UserEventType eventType;
    private LocalDateTime timestamp;

    public enum UserEventType {
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_ACTIVATED,
        USER_DEACTIVATED,
        EMAIL_VERIFIED,
        PHONE_VERIFIED
    }
} 