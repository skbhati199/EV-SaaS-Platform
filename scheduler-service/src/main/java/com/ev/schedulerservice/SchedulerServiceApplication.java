package com.ev.schedulerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
public class SchedulerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerServiceApplication.class, args);
    }

}
