package com.ev.billingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${user.service.url:http://localhost:8085}")
public interface UserClient {
    
    @GetMapping("/api/v1/users/{userId}/email")
    String getUserEmailById(@PathVariable UUID userId);
    
} 