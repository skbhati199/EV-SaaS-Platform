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
public class MeterValue {
    private LocalDateTime timestamp;
    private List<SampledValue> sampledValue;
} 