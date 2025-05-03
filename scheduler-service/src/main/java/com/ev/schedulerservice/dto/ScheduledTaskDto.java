package com.ev.schedulerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskDto {
    private Long id;
    private String name;
    private String description;
    private String taskType;
    private String cronExpression;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 