package com.smsplatform.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Configuration Server for SMS Monitoring Platform
 * 
 * This service provides centralized configuration management for all microservices
 * in the SMS monitoring platform. It serves configuration properties from a Git
 * repository or local file system.
 * 
 * @author SMS Platform Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}

