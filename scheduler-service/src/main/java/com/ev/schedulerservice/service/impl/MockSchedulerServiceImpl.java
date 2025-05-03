package com.ev.schedulerservice.service.impl;

import com.ev.schedulerservice.dto.ScheduledTaskDto;
import com.ev.schedulerservice.service.SchedulerService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Profile("docker")
@Primary
public class MockSchedulerServiceImpl implements SchedulerService {
    
    private final Map<Long, ScheduledTaskDto> tasksMap = new HashMap<>();
    private Long nextId = 1L;
    
    // Initialize with some sample data
    public MockSchedulerServiceImpl() {
        // Create some sample tasks
        ScheduledTaskDto task1 = new ScheduledTaskDto();
        task1.setId(nextId++);
        task1.setName("Daily Report Generation");
        task1.setDescription("Generate daily usage reports");
        task1.setTaskType("REPORT");
        task1.setCronExpression("0 0 0 * * ?");
        task1.setActive(true);
        task1.setCreatedAt(LocalDateTime.now().minusDays(10));
        task1.setUpdatedAt(LocalDateTime.now().minusDays(5));
        
        ScheduledTaskDto task2 = new ScheduledTaskDto();
        task2.setId(nextId++);
        task2.setName("Weekly Billing");
        task2.setDescription("Process weekly billing for customers");
        task2.setTaskType("BILLING");
        task2.setCronExpression("0 0 12 ? * SUN");
        task2.setActive(true);
        task2.setCreatedAt(LocalDateTime.now().minusDays(20));
        task2.setUpdatedAt(LocalDateTime.now().minusDays(2));
        
        ScheduledTaskDto task3 = new ScheduledTaskDto();
        task3.setId(nextId++);
        task3.setName("Station Health Check");
        task3.setDescription("Check health status of all stations");
        task3.setTaskType("MONITORING");
        task3.setCronExpression("0 */30 * * * ?");
        task3.setActive(false);
        task3.setCreatedAt(LocalDateTime.now().minusDays(5));
        task3.setUpdatedAt(LocalDateTime.now().minusDays(1));
        
        tasksMap.put(task1.getId(), task1);
        tasksMap.put(task2.getId(), task2);
        tasksMap.put(task3.getId(), task3);
    }
    
    @Override
    public ScheduledTaskDto createTask(ScheduledTaskDto taskDto) {
        taskDto.setId(nextId++);
        taskDto.setCreatedAt(LocalDateTime.now());
        taskDto.setUpdatedAt(LocalDateTime.now());
        tasksMap.put(taskDto.getId(), taskDto);
        return taskDto;
    }
    
    @Override
    public ScheduledTaskDto updateTask(Long id, ScheduledTaskDto taskDto) {
        if (!tasksMap.containsKey(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        
        ScheduledTaskDto existingTask = tasksMap.get(id);
        existingTask.setName(taskDto.getName());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setTaskType(taskDto.getTaskType());
        existingTask.setCronExpression(taskDto.getCronExpression());
        existingTask.setActive(taskDto.isActive());
        existingTask.setUpdatedAt(LocalDateTime.now());
        
        return existingTask;
    }
    
    @Override
    public ScheduledTaskDto getTaskById(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        return tasksMap.get(id);
    }
    
    @Override
    public List<ScheduledTaskDto> getAllTasks() {
        return new ArrayList<>(tasksMap.values());
    }
    
    @Override
    public List<ScheduledTaskDto> getActiveTasks() {
        return tasksMap.values().stream()
                .filter(ScheduledTaskDto::isActive)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ScheduledTaskDto> getTasksByType(String taskType) {
        return tasksMap.values().stream()
                .filter(task -> task.getTaskType().equalsIgnoreCase(taskType))
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteTask(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        tasksMap.remove(id);
    }
    
    @Override
    public void runTaskNow(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        // In a real implementation, this would trigger the task
        System.out.println("Task " + id + " executed manually at " + LocalDateTime.now());
    }
    
    @Override
    public void activateTask(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        ScheduledTaskDto task = tasksMap.get(id);
        task.setActive(true);
        task.setUpdatedAt(LocalDateTime.now());
    }
    
    @Override
    public void deactivateTask(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        ScheduledTaskDto task = tasksMap.get(id);
        task.setActive(false);
        task.setUpdatedAt(LocalDateTime.now());
    }
} 