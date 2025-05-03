package com.ev.schedulerservice.controller;

import com.ev.schedulerservice.dto.ScheduledTaskDto;
import com.ev.schedulerservice.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduler")
public class SchedulerController {

    @Autowired
    private SchedulerService schedulerService;

    @PostMapping("/tasks")
    public ResponseEntity<ScheduledTaskDto> createTask(@RequestBody ScheduledTaskDto taskDto) {
        return new ResponseEntity<>(schedulerService.createTask(taskDto), HttpStatus.CREATED);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<ScheduledTaskDto> updateTask(@PathVariable Long id, @RequestBody ScheduledTaskDto taskDto) {
        return ResponseEntity.ok(schedulerService.updateTask(id, taskDto));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<ScheduledTaskDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(schedulerService.getTaskById(id));
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<ScheduledTaskDto>> getAllTasks() {
        return ResponseEntity.ok(schedulerService.getAllTasks());
    }

    @GetMapping("/tasks/active")
    public ResponseEntity<List<ScheduledTaskDto>> getActiveTasks() {
        return ResponseEntity.ok(schedulerService.getActiveTasks());
    }

    @GetMapping("/tasks/type/{taskType}")
    public ResponseEntity<List<ScheduledTaskDto>> getTasksByType(@PathVariable String taskType) {
        return ResponseEntity.ok(schedulerService.getTasksByType(taskType));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        schedulerService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks/{id}/run")
    public ResponseEntity<Void> runTaskNow(@PathVariable Long id) {
        schedulerService.runTaskNow(id);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/tasks/{id}/activate")
    public ResponseEntity<Void> activateTask(@PathVariable Long id) {
        schedulerService.activateTask(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/tasks/{id}/deactivate")
    public ResponseEntity<Void> deactivateTask(@PathVariable Long id) {
        schedulerService.deactivateTask(id);
        return ResponseEntity.ok().build();
    }
} 