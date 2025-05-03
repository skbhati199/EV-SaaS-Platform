package com.ev.station.dto;

import com.ev.station.model.StationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatRequest {
    
    @NotNull(message = "Status is required")
    private StationStatus status;
    
    private LocalDateTime timestamp;
} 