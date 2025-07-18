-- SMS Monitoring Platform Database Schema

CREATE DATABASE IF NOT EXISTS sms_monitoring;
USE sms_monitoring;

-- Users table for authentication
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'OPERATOR', 'VIEWER') DEFAULT 'VIEWER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Telecom operators table
CREATE TABLE operators (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) UNIQUE NOT NULL,
    country VARCHAR(50) NOT NULL,
    api_endpoint VARCHAR(255),
    api_key VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SMS messages table
CREATE TABLE sms_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(100) UNIQUE NOT NULL,
    operator_id BIGINT,
    sender_number VARCHAR(20) NOT NULL,
    recipient_number VARCHAR(20) NOT NULL,
    message_content TEXT NOT NULL,
    status ENUM('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'EXPIRED') DEFAULT 'PENDING',
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL',
    scheduled_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (operator_id) REFERENCES operators(id)
);

-- Network simulation tests table
CREATE TABLE network_tests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_name VARCHAR(100) NOT NULL,
    operator_id BIGINT,
    test_type ENUM('LOAD', 'STRESS', 'LATENCY', 'THROUGHPUT') NOT NULL,
    configuration JSON,
    status ENUM('PENDING', 'RUNNING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    results JSON,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (operator_id) REFERENCES operators(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- System metrics table
CREATE TABLE system_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(50) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15,4) NOT NULL,
    unit VARCHAR(20),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_service_metric (service_name, metric_name),
    INDEX idx_timestamp (timestamp)
);

-- Alerts table
CREATE TABLE alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_type ENUM('ERROR', 'WARNING', 'INFO') NOT NULL,
    service_name VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    details JSON,
    status ENUM('ACTIVE', 'ACKNOWLEDGED', 'RESOLVED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    acknowledged_at TIMESTAMP NULL,
    acknowledged_by BIGINT NULL,
    resolved_at TIMESTAMP NULL,
    FOREIGN KEY (acknowledged_by) REFERENCES users(id)
);

-- Insert sample data
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@smsplatform.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN'),
('operator1', 'operator1@smsplatform.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'OPERATOR'),
('viewer1', 'viewer1@smsplatform.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'VIEWER');

INSERT INTO operators (name, code, country, api_endpoint) VALUES
('Telecom Alpha', 'TCA', 'Saudi Arabia', 'https://api.telecomalpha.sa'),
('Mobile Beta', 'MBT', 'UAE', 'https://api.mobilebeta.ae'),
('Network Gamma', 'NGM', 'Egypt', 'https://api.networkgamma.eg');

-- Create indexes for better performance
CREATE INDEX idx_sms_status ON sms_messages(status);
CREATE INDEX idx_sms_created_at ON sms_messages(created_at);
CREATE INDEX idx_sms_operator ON sms_messages(operator_id);
CREATE INDEX idx_test_status ON network_tests(status);
CREATE INDEX idx_alert_status ON alerts(status);
CREATE INDEX idx_alert_created_at ON alerts(created_at);

