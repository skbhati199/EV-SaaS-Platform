package com.ev.billingservice.controller;

import com.ev.billingservice.dto.UsageRecordDTO;
import com.ev.billingservice.service.UsageRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/usage-records")
@RequiredArgsConstructor
public class UsageRecordController {
    
    private final UsageRecordService usageRecordService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<UsageRecordDTO> createUsageRecord(@Valid @RequestBody UsageRecordDTO usageRecordDTO) {
        return new ResponseEntity<>(usageRecordService.createUsageRecord(usageRecordDTO), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsageRecordDTO> getUsageRecordById(@PathVariable UUID id) {
        return ResponseEntity.ok(usageRecordService.getUsageRecordById(id));
    }
    
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<UsageRecordDTO>> getUsageRecordsBySubscriptionId(@PathVariable UUID subscriptionId) {
        return ResponseEntity.ok(usageRecordService.getUsageRecordsBySubscriptionId(subscriptionId));
    }
    
    @GetMapping("/subscription/{subscriptionId}/meter-type/{meterType}")
    public ResponseEntity<List<UsageRecordDTO>> getUsageRecordsBySubscriptionIdAndMeterType(
            @PathVariable UUID subscriptionId, @PathVariable String meterType) {
        return ResponseEntity.ok(usageRecordService.getUsageRecordsBySubscriptionIdAndMeterType(subscriptionId, meterType));
    }
    
    @GetMapping("/subscription/{subscriptionId}/unprocessed")
    public ResponseEntity<List<UsageRecordDTO>> getUnprocessedUsageRecordsBySubscriptionId(@PathVariable UUID subscriptionId) {
        return ResponseEntity.ok(usageRecordService.getUnprocessedUsageRecordsBySubscriptionId(subscriptionId));
    }
    
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsageRecordDTO>> getUsageRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(usageRecordService.getUsageRecordsByDateRange(start, end));
    }
    
    @GetMapping("/subscription/{subscriptionId}/date-range")
    public ResponseEntity<List<UsageRecordDTO>> getUsageRecordsBySubscriptionIdAndDateRange(
            @PathVariable UUID subscriptionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(usageRecordService.getUsageRecordsBySubscriptionIdAndDateRange(subscriptionId, start, end));
    }
    
    @PutMapping("/{id}/mark-processed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsageRecordDTO> markUsageRecordAsProcessed(@PathVariable UUID id) {
        return ResponseEntity.ok(usageRecordService.markUsageRecordAsProcessed(id));
    }
    
    @PostMapping("/process-for-billing")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> processUsageRecordsForBilling() {
        usageRecordService.processUsageRecordsForBilling();
        return ResponseEntity.ok().build();
    }
} 