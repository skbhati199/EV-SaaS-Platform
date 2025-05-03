package com.ev.station.ocpp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BootNotificationResponse {
    private RegistrationStatus status;
    private LocalDateTime currentTime;
    private int interval; // Heartbeat interval in seconds
} 