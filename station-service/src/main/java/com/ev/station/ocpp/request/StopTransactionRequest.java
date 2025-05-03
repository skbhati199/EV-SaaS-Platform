package com.ev.station.ocpp.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopTransactionRequest {
    private String idTag;
    private int meterStop;
    private LocalDateTime timestamp;
    private int transactionId;
    private String reason;
    private List<MeterValue> transactionData;
} 