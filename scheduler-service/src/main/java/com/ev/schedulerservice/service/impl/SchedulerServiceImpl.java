package com.ev.schedulerservice.service.impl;

import com.ev.schedulerservice.dto.ScheduledTaskDto;
import com.ev.schedulerservice.model.ScheduledTask;
import com.ev.schedulerservice.repository.ScheduledTaskRepository;
import com.ev.schedulerservice.service.SchedulerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private ScheduledTaskRepository taskRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ScheduledTaskDto createTask(ScheduledTaskDto taskDto) {
        ScheduledTask task = convertToEntity(taskDto);
        task.setLastExecutionStatus("PENDING");
        task = taskRepository.save(task);
        return convertToDto(task);
    }

    @Override
    public ScheduledTaskDto updateTask(Long id, ScheduledTaskDto taskDto) {
        ScheduledTask existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        BeanUtils.copyProperties(taskDto, existingTask, "id");
        existingTask = taskRepository.save(existingTask);
        return convertToDto(existingTask);
    }

    @Override
    public ScheduledTaskDto getTaskById(Long id) {
        ScheduledTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return convertToDto(task);
    }

    @Override
    public List<ScheduledTaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledTaskDto> getActiveTasks() {
        return taskRepository.findByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledTaskDto> getTasksByType(String taskType) {
        return taskRepository.findByTaskType(taskType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public void runTaskNow(Long id) {
        ScheduledTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        executeTask(task);
    }

    @Override
    public void activateTask(Long id) {
        ScheduledTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setActive(true);
        taskRepository.save(task);
    }

    @Override
    public void deactivateTask(Long id) {
        ScheduledTask task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setActive(false);
        taskRepository.save(task);
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void executeScheduledTasks() {
        List<ScheduledTask> activeTasks = taskRepository.findByActiveTrue();
        
        for (ScheduledTask task : activeTasks) {
            if (shouldExecuteTask(task)) {
                executeTask(task);
            }
        }
    }
    
    private boolean shouldExecuteTask(ScheduledTask task) {
        // For one-time tasks
        if (task.getScheduledTime() != null) {
            return LocalDateTime.now().isAfter(task.getScheduledTime()) && 
                   (task.getLastExecutionTime() == null || 
                    "FAILED".equals(task.getLastExecutionStatus()));
        }
        
        // For recurring tasks, we would need to parse and evaluate the cron expression
        // This is a simplified version that just returns true
        // In a real implementation, you would use CronExpression or similar to check timing
        return true;
    }
    
    private void executeTask(ScheduledTask task) {
        task.setLastExecutionTime(LocalDateTime.now());
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String url = task.getTargetServiceUrl() + task.getTargetEndpoint();
            HttpEntity<Map<String, String>> request = new HttpEntity<>(task.getParameters(), headers);
            
            restTemplate.postForEntity(url, request, String.class);
            
            task.setLastExecutionStatus("SUCCESS");
        } catch (Exception e) {
            task.setLastExecutionStatus("FAILED: " + e.getMessage());
        }
        
        taskRepository.save(task);
    }
    
    private ScheduledTaskDto convertToDto(ScheduledTask task) {
        ScheduledTaskDto dto = new ScheduledTaskDto();
        BeanUtils.copyProperties(task, dto);
        return dto;
    }
    
    private ScheduledTask convertToEntity(ScheduledTaskDto dto) {
        ScheduledTask entity = new ScheduledTask();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
} 