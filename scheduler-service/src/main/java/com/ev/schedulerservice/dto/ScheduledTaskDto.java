package com.ev.schedulerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskDto {
    private Long id;
    private String taskName;
    private String taskType;
    private String cronExpression;
    private LocalDateTime scheduledTime;
    private Boolean active;
    private String targetServiceUrl;
    private String targetEndpoint;
    private Map<String, String> parameters;
    private LocalDateTime lastExecutionTime;
    private String lastExecutionStatus;
} 