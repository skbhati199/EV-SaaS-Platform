package com.ev.station.dto;

import com.ev.station.model.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingSessionDto {
    private UUID id;
    private UUID stationId;
    private Integer connectorId;
    private String transactionId;
    private String idTag;
    private UUID userId;
    private LocalDateTime startTimestamp;
    private LocalDateTime stopTimestamp;
    private Integer meterStart;
    private Integer meterStop;
    private String startReason;
    private String stopReason;
    private BigDecimal totalEnergyKwh;
    private BigDecimal totalCost;
    private String currency;
    private SessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 