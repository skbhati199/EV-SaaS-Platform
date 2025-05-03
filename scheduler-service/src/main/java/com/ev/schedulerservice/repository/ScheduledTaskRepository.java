package com.ev.schedulerservice.repository;

import com.ev.schedulerservice.model.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
    
    List<ScheduledTask> findByActiveTrue();
    
    List<ScheduledTask> findByTaskType(String taskType);
    
    List<ScheduledTask> findByTaskTypeAndActiveTrue(String taskType);
} 