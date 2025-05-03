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
public class IdTagInfo {
    private AuthorizationStatus status;
    private LocalDateTime expiryDate;
    private String parentIdTag;
} 