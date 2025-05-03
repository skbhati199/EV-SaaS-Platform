package com.ev.userservice.dto;

import com.ev.userservice.model.ChargingHistory;
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
public class ChargingHistoryDto {
    private UUID id;
    private UUID userId;
    private UUID sessionId;
    private UUID stationId;
    private Integer connectorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal energyConsumedKwh;
    private BigDecimal cost;
    private String currency;
    private ChargingHistory.PaymentStatus paymentStatus;
} 