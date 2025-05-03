package com.ev.schedulerservice.repository;

import com.ev.schedulerservice.model.ScheduledTask;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("docker")
@Primary
public class MockScheduledTaskRepository implements ScheduledTaskRepository {
    
    private final Map<Long, ScheduledTask> tasksMap = new HashMap<>();
    private Long nextId = 1L;
    
    // Initialize with some sample data
    public MockScheduledTaskRepository() {
        // Create some sample tasks
        ScheduledTask task1 = ScheduledTask.builder()
                .id(nextId++)
                .taskName("Daily Report Generation")
                .taskType("REPORT")
                .cronExpression("0 0 0 * * ?")
                .active(true)
                .targetServiceUrl("http://reporting-service")
                .targetEndpoint("/api/v1/reports/generate")
                .parameters(Map.of("type", "daily", "format", "pdf"))
                .lastExecutionTime(LocalDateTime.now().minusDays(1))
                .lastExecutionStatus("SUCCESS")
                .build();
        
        ScheduledTask task2 = ScheduledTask.builder()
                .id(nextId++)
                .taskName("Weekly Billing")
                .taskType("BILLING")
                .cronExpression("0 0 12 ? * SUN")
                .active(true)
                .targetServiceUrl("http://billing-service")
                .targetEndpoint("/api/v1/billing/process")
                .parameters(Map.of("cycle", "weekly"))
                .lastExecutionTime(LocalDateTime.now().minusWeeks(1))
                .lastExecutionStatus("SUCCESS")
                .build();
        
        ScheduledTask task3 = ScheduledTask.builder()
                .id(nextId++)
                .taskName("Station Health Check")
                .taskType("MONITORING")
                .cronExpression("0 */30 * * * ?")
                .active(false)
                .targetServiceUrl("http://station-service")
                .targetEndpoint("/api/v1/stations/health-check")
                .parameters(Map.of("detailed", "true"))
                .lastExecutionTime(LocalDateTime.now().minusHours(12))
                .lastExecutionStatus("FAILED: Connection timeout")
                .build();
        
        tasksMap.put(task1.getId(), task1);
        tasksMap.put(task2.getId(), task2);
        tasksMap.put(task3.getId(), task3);
    }
    
    @Override
    public List<ScheduledTask> findByActiveTrue() {
        return tasksMap.values().stream()
                .filter(ScheduledTask::getActive)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ScheduledTask> findByTaskType(String taskType) {
        return tasksMap.values().stream()
                .filter(task -> task.getTaskType().equals(taskType))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ScheduledTask> findByTaskTypeAndActiveTrue(String taskType) {
        return tasksMap.values().stream()
                .filter(task -> task.getTaskType().equals(taskType) && task.getActive())
                .collect(Collectors.toList());
    }
    
    @Override
    public <S extends ScheduledTask> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(nextId++);
        }
        tasksMap.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<ScheduledTask> findById(Long id) {
        return Optional.ofNullable(tasksMap.get(id));
    }
    
    @Override
    public List<ScheduledTask> findAll() {
        return new ArrayList<>(tasksMap.values());
    }
    
    @Override
    public void deleteById(Long id) {
        tasksMap.remove(id);
    }
    
    // Unimplemented methods
    
    @Override
    public boolean existsById(Long id) {
        return tasksMap.containsKey(id);
    }
    
    @Override
    public <S extends ScheduledTask> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        entities.forEach(entity -> result.add(save(entity)));
        return result;
    }
    
    @Override
    public void flush() {
        // No-op in mock implementation
    }
    
    @Override
    public <S extends ScheduledTask> S saveAndFlush(S entity) {
        return save(entity);
    }
    
    @Override
    public <S extends ScheduledTask> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }
    
    @Override
    public void deleteAllInBatch(Iterable<ScheduledTask> entities) {
        entities.forEach(entity -> tasksMap.remove(entity.getId()));
    }
    
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        ids.forEach(tasksMap::remove);
    }
    
    @Override
    public void deleteAllInBatch() {
        tasksMap.clear();
    }
    
    @Override
    public ScheduledTask getOne(Long id) {
        return tasksMap.get(id);
    }
    
    @Override
    public ScheduledTask getById(Long id) {
        return tasksMap.get(id);
    }
    
    @Override
    public ScheduledTask getReferenceById(Long id) {
        return tasksMap.get(id);
    }
    
    @Override
    public <S extends ScheduledTask> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }
    
    @Override
    public <S extends ScheduledTask> List<S> findAll(Example<S> example) {
        return Collections.emptyList();
    }
    
    @Override
    public <S extends ScheduledTask> List<S> findAll(Example<S> example, Sort sort) {
        return Collections.emptyList();
    }
    
    @Override
    public <S extends ScheduledTask> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }
    
    @Override
    public <S extends ScheduledTask> long count(Example<S> example) {
        return 0;
    }
    
    @Override
    public <S extends ScheduledTask> boolean exists(Example<S> example) {
        return false;
    }
    
    @Override
    public <S extends ScheduledTask, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
    
    @Override
    public List<ScheduledTask> findAll(Sort sort) {
        return new ArrayList<>(tasksMap.values());
    }
    
    @Override
    public Page<ScheduledTask> findAll(Pageable pageable) {
        return Page.empty();
    }
    
    @Override
    public long count() {
        return tasksMap.size();
    }
    
    @Override
    public void delete(ScheduledTask entity) {
        tasksMap.remove(entity.getId());
    }
    
    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(tasksMap::remove);
    }
    
    @Override
    public void deleteAll(Iterable<? extends ScheduledTask> entities) {
        entities.forEach(entity -> tasksMap.remove(entity.getId()));
    }
    
    @Override
    public void deleteAll() {
        tasksMap.clear();
    }
    
    @Override
    public List<ScheduledTask> findAllById(Iterable<Long> ids) {
        return StreamSupport.stream(ids.spliterator(), false)
                .map(tasksMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
} 