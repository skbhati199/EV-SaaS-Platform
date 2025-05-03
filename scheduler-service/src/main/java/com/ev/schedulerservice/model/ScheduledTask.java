package com.ev.schedulerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "scheduled_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String taskName;

    @Column(nullable = false)
    private String taskType; // CRON, V2G, CHARGING, NOTIFICATION, etc.

    @Column(nullable = false)
    private String cronExpression; // For scheduled tasks

    @Column
    private LocalDateTime scheduledTime; // For one-time tasks

    @Column(nullable = false)
    private Boolean active;

    @Column
    private String targetServiceUrl; // The microservice to call

    @Column
    private String targetEndpoint; // The specific endpoint to call

    @ElementCollection
    @CollectionTable(name = "task_parameters", joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "param_key")
    @Column(name = "param_value")
    private Map<String, String> parameters; // JSON parameters for the task

    @Column
    private LocalDateTime lastExecutionTime;

    @Column
    private String lastExecutionStatus;
} 