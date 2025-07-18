package com.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Task Manager API Application
 * 
 * A comprehensive RESTful API for task management with JWT authentication.
 * Features include user management, task CRUD operations, role-based access control,
 * and comprehensive security measures.
 * 
 * @author Task Manager Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}

