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
public class StartTransactionRequest {
    private int connectorId;
    private String idTag;
    private int meterStart;
    private Integer reservationId;
    private LocalDateTime timestamp;
} 