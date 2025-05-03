package com.ev.station.ocpp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopTransactionResponse {
    private IdTagInfo idTagInfo;
} 