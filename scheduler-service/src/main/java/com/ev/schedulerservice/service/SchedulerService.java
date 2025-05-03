package com.ev.schedulerservice.service;

import com.ev.schedulerservice.dto.ScheduledTaskDto;

import java.util.List;

public interface SchedulerService {
    
    ScheduledTaskDto createTask(ScheduledTaskDto taskDto);
    
    ScheduledTaskDto updateTask(Long id, ScheduledTaskDto taskDto);
    
    ScheduledTaskDto getTaskById(Long id);
    
    List<ScheduledTaskDto> getAllTasks();
    
    List<ScheduledTaskDto> getActiveTasks();
    
    List<ScheduledTaskDto> getTasksByType(String taskType);
    
    void deleteTask(Long id);
    
    void runTaskNow(Long id);
    
    void activateTask(Long id);
    
    void deactivateTask(Long id);
} 