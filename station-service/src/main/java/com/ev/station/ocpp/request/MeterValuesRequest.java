package com.ev.station.ocpp.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterValuesRequest {
    private int connectorId;
    private Integer transactionId;
    private List<MeterValue> meterValue;
} 