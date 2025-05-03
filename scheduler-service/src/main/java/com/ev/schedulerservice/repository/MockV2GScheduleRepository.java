package com.ev.schedulerservice.repository;

import com.ev.schedulerservice.model.V2GSchedule;
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
public class MockV2GScheduleRepository implements V2GScheduleRepository {
    
    private final Map<Long, V2GSchedule> schedulesMap = new HashMap<>();
    private Long nextId = 1L;
    
    // Initialize with some sample data
    public MockV2GScheduleRepository() {
        // Sample data for V2G schedules
        V2GSchedule schedule1 = V2GSchedule.builder()
                .id(nextId++)
                .vehicleId("vehicle-001")
                .stationId("station-001")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(3))
                .powerKw(11.0) // Charging
                .scheduleType("PEAK_SHAVING")
                .status("SCHEDULED")
                .lastUpdated(LocalDateTime.now().minusHours(1))
                .userId("user-001")
                .build();
        
        V2GSchedule schedule2 = V2GSchedule.builder()
                .id(nextId++)
                .vehicleId("vehicle-002")
                .stationId("station-002")
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1))
                .powerKw(-7.0) // Discharging
                .scheduleType("GRID_BALANCING")
                .status("IN_PROGRESS")
                .lastUpdated(LocalDateTime.now().minusMinutes(30))
                .userId("user-002")
                .build();
        
        V2GSchedule schedule3 = V2GSchedule.builder()
                .id(nextId++)
                .vehicleId("vehicle-003")
                .stationId("station-001")
                .startTime(LocalDateTime.now().minusHours(3))
                .endTime(LocalDateTime.now().minusHours(1))
                .powerKw(22.0) // Fast charging
                .scheduleType("DEMAND_RESPONSE")
                .status("COMPLETED")
                .lastUpdated(LocalDateTime.now().minusHours(1))
                .userId("user-003")
                .build();
        
        schedulesMap.put(schedule1.getId(), schedule1);
        schedulesMap.put(schedule2.getId(), schedule2);
        schedulesMap.put(schedule3.getId(), schedule3);
    }
    
    @Override
    public List<V2GSchedule> findByVehicleId(String vehicleId) {
        return schedulesMap.values().stream()
                .filter(schedule -> schedule.getVehicleId().equals(vehicleId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GSchedule> findByStationId(String stationId) {
        return schedulesMap.values().stream()
                .filter(schedule -> schedule.getStationId().equals(stationId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GSchedule> findByUserId(String userId) {
        return schedulesMap.values().stream()
                .filter(schedule -> schedule.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GSchedule> findByStatus(String status) {
        return schedulesMap.values().stream()
                .filter(schedule -> schedule.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GSchedule> findSchedulesInTimeRange(LocalDateTime start, LocalDateTime end) {
        return schedulesMap.values().stream()
                .filter(schedule -> 
                    (schedule.getStartTime().isAfter(start) || schedule.getStartTime().isEqual(start)) && 
                    (schedule.getEndTime().isBefore(end) || schedule.getEndTime().isEqual(end)))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GSchedule> findSchedulesForStationInTimeRange(String stationId, LocalDateTime start, LocalDateTime end) {
        return schedulesMap.values().stream()
                .filter(schedule -> 
                    schedule.getStationId().equals(stationId) &&
                    (schedule.getStartTime().isAfter(start) || schedule.getStartTime().isEqual(start)) && 
                    (schedule.getEndTime().isBefore(end) || schedule.getEndTime().isEqual(end)))
                .collect(Collectors.toList());
    }
    
    // Standard JpaRepository methods
    
    @Override
    public <S extends V2GSchedule> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(nextId++);
        }
        schedulesMap.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<V2GSchedule> findById(Long id) {
        return Optional.ofNullable(schedulesMap.get(id));
    }
    
    @Override
    public boolean existsById(Long id) {
        return schedulesMap.containsKey(id);
    }
    
    @Override
    public List<V2GSchedule> findAll() {
        return new ArrayList<>(schedulesMap.values());
    }
    
    @Override
    public List<V2GSchedule> findAllById(Iterable<Long> ids) {
        return StreamSupport.stream(ids.spliterator(), false)
                .map(schedulesMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return schedulesMap.size();
    }
    
    @Override
    public void deleteById(Long id) {
        schedulesMap.remove(id);
    }
    
    @Override
    public void delete(V2GSchedule entity) {
        schedulesMap.remove(entity.getId());
    }
    
    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(schedulesMap::remove);
    }
    
    @Override
    public void deleteAll(Iterable<? extends V2GSchedule> entities) {
        entities.forEach(entity -> schedulesMap.remove(entity.getId()));
    }
    
    @Override
    public void deleteAll() {
        schedulesMap.clear();
    }
    
    // Other required methods
    
    @Override
    public <S extends V2GSchedule> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        entities.forEach(entity -> result.add(save(entity)));
        return result;
    }
    
    @Override
    public void flush() {
        // No-op in mock implementation
    }
    
    @Override
    public <S extends V2GSchedule> S saveAndFlush(S entity) {
        return save(entity);
    }
    
    @Override
    public <S extends V2GSchedule> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }
    
    @Override
    public void deleteAllInBatch(Iterable<V2GSchedule> entities) {
        entities.forEach(entity -> schedulesMap.remove(entity.getId()));
    }
    
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        ids.forEach(schedulesMap::remove);
    }
    
    @Override
    public void deleteAllInBatch() {
        schedulesMap.clear();
    }
    
    @Override
    public V2GSchedule getOne(Long id) {
        return schedulesMap.get(id);
    }
    
    @Override
    public V2GSchedule getById(Long id) {
        return schedulesMap.get(id);
    }
    
    @Override
    public V2GSchedule getReferenceById(Long id) {
        return schedulesMap.get(id);
    }
    
    @Override
    public <S extends V2GSchedule> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }
    
    @Override
    public <S extends V2GSchedule> List<S> findAll(Example<S> example) {
        return Collections.emptyList();
    }
    
    @Override
    public <S extends V2GSchedule> List<S> findAll(Example<S> example, Sort sort) {
        return Collections.emptyList();
    }
    
    @Override
    public <S extends V2GSchedule> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }
    
    @Override
    public <S extends V2GSchedule> long count(Example<S> example) {
        return 0;
    }
    
    @Override
    public <S extends V2GSchedule> boolean exists(Example<S> example) {
        return false;
    }
    
    @Override
    public <S extends V2GSchedule, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
    
    @Override
    public List<V2GSchedule> findAll(Sort sort) {
        return new ArrayList<>(schedulesMap.values());
    }
    
    @Override
    public Page<V2GSchedule> findAll(Pageable pageable) {
        return Page.empty();
    }
} 