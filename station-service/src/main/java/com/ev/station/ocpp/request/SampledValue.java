package com.ev.station.ocpp.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampledValue {
    private String value;
    private String context;
    private String format;
    private String measurand;
    private String phase;
    private String location;
    private String unit;
} 