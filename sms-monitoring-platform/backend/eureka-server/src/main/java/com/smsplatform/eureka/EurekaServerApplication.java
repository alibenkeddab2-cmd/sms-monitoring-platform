package com.smsplatform.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Service Discovery Server for SMS Monitoring Platform
 * 
 * This service provides service registration and discovery capabilities
 * for all microservices in the SMS monitoring platform. It maintains
 * a registry of all available services and their locations.
 * 
 * @author SMS Platform Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

