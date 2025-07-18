package com.smsplatform.sms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Telecom Operator Entity
 * 
 * Represents a telecom operator in the system with configuration
 * details for API integration and monitoring.
 */
@Entity
@Table(name = "operators")
@EntityListeners(AuditingEntityListener.class)
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Operator name is required")
    @Size(max = 100, message = "Operator name must not exceed 100 characters")
    private String name;

    @Column(name = "code", unique = true, nullable = false)
    @NotBlank(message = "Operator code is required")
    @Size(max = 10, message = "Operator code must not exceed 10 characters")
    private String code;

    @Column(name = "country", nullable = false)
    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    @Column(name = "api_endpoint")
    @Size(max = 255, message = "API endpoint must not exceed 255 characters")
    private String apiEndpoint;

    @Column(name = "api_key")
    @Size(max = 255, message = "API key must not exceed 255 characters")
    private String apiKey;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Operator() {}

    public Operator(String name, String code, String country) {
        this.name = name;
        this.code = code;
        this.country = country;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

