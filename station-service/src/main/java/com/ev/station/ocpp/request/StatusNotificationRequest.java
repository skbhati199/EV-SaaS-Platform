package com.ev.station.ocpp.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusNotificationRequest {
    private int connectorId;
    private String errorCode;
    private String info;
    private String status;
    private LocalDateTime timestamp;
    private String vendorId;
    private String vendorErrorCode;
} 