package com.ev.station.ocpp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartTransactionResponse {
    private IdTagInfo idTagInfo;
    private int transactionId;
} 