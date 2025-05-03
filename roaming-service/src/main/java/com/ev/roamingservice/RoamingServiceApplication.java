package com.ev.roamingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Profile;

/**
 * Main application class for Roaming Service
 * For local development, various auto-configurations are disabled to allow running without dependencies
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    SecurityAutoConfiguration.class
})
@EnableDiscoveryClient(autoRegister = false)
public class RoamingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoamingServiceApplication.class, args);
    }
}
