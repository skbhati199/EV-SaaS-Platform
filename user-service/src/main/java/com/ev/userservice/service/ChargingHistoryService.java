package com.ev.userservice.service;

import com.ev.userservice.dto.ChargingHistoryDto;
import com.ev.userservice.dto.RecordChargingSessionRequest;
import com.ev.userservice.model.ChargingHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChargingHistoryService {
    List<ChargingHistoryDto> getChargingHistoryByUserId(UUID userId);
    ChargingHistoryDto getChargingHistoryById(UUID id);
    ChargingHistoryDto getChargingHistoryBySessionId(UUID sessionId);
    ChargingHistoryDto recordChargingSession(UUID userId, RecordChargingSessionRequest request);
    ChargingHistoryDto updatePaymentStatus(UUID id, ChargingHistory.PaymentStatus status);
    List<ChargingHistoryDto> getChargingHistoryByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate);
} 