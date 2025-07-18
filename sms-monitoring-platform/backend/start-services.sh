#!/bin/bash

# SMS Monitoring Platform - Service Startup Script
# This script starts all backend services in the correct order

echo "Starting SMS Monitoring Platform Services..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if service is running
check_service() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=1
    
    echo -e "${YELLOW}Waiting for $service_name to start on port $port...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
            echo -e "${GREEN}$service_name is running!${NC}"
            return 0
        fi
        
        echo "Attempt $attempt/$max_attempts - $service_name not ready yet..."
        sleep 5
        ((attempt++))
    done
    
    echo -e "${RED}$service_name failed to start within expected time!${NC}"
    return 1
}

# Start Config Server first
echo -e "${YELLOW}Starting Config Server...${NC}"
cd config-server
./mvnw spring-boot:run > ../logs/config-server.log 2>&1 &
CONFIG_PID=$!
cd ..

# Wait for Config Server to be ready
if ! check_service "Config Server" 8888; then
    echo -e "${RED}Config Server failed to start. Exiting...${NC}"
    exit 1
fi

# Start Eureka Server
echo -e "${YELLOW}Starting Eureka Server...${NC}"
cd eureka-server
./mvnw spring-boot:run > ../logs/eureka-server.log 2>&1 &
EUREKA_PID=$!
cd ..

# Wait for Eureka Server to be ready
if ! check_service "Eureka Server" 8761; then
    echo -e "${RED}Eureka Server failed to start. Exiting...${NC}"
    exit 1
fi

# Start Gateway Service
echo -e "${YELLOW}Starting Gateway Service...${NC}"
cd gateway-service
./mvnw spring-boot:run > ../logs/gateway-service.log 2>&1 &
GATEWAY_PID=$!
cd ..

# Wait for Gateway Service to be ready
if ! check_service "Gateway Service" 8080; then
    echo -e "${RED}Gateway Service failed to start. Exiting...${NC}"
    exit 1
fi

# Start SMS Service
echo -e "${YELLOW}Starting SMS Service...${NC}"
cd sms-service
./mvnw spring-boot:run > ../logs/sms-service.log 2>&1 &
SMS_PID=$!
cd ..

# Wait for SMS Service to be ready
if ! check_service "SMS Service" 8081; then
    echo -e "${RED}SMS Service failed to start. Exiting...${NC}"
    exit 1
fi

# Start Notification Service
echo -e "${YELLOW}Starting Notification Service...${NC}"
cd notification-service
./mvnw spring-boot:run > ../logs/notification-service.log 2>&1 &
NOTIFICATION_PID=$!
cd ..

# Start Monitoring Service
echo -e "${YELLOW}Starting Monitoring Service...${NC}"
cd monitoring-service
./mvnw spring-boot:run > ../logs/monitoring-service.log 2>&1 &
MONITORING_PID=$!
cd ..

# Create PID file for easy service management
echo "CONFIG_PID=$CONFIG_PID" > service-pids.txt
echo "EUREKA_PID=$EUREKA_PID" >> service-pids.txt
echo "GATEWAY_PID=$GATEWAY_PID" >> service-pids.txt
echo "SMS_PID=$SMS_PID" >> service-pids.txt
echo "NOTIFICATION_PID=$NOTIFICATION_PID" >> service-pids.txt
echo "MONITORING_PID=$MONITORING_PID" >> service-pids.txt

echo -e "${GREEN}"
echo "=========================================="
echo "All services started successfully!"
echo "=========================================="
echo "Config Server:      http://localhost:8888"
echo "Eureka Server:      http://localhost:8761"
echo "Gateway Service:    http://localhost:8080"
echo "SMS Service:        http://localhost:8081"
echo "API Documentation:  http://localhost:8080/swagger-ui.html"
echo "=========================================="
echo -e "${NC}"

echo "Service PIDs saved to service-pids.txt"
echo "To stop all services, run: ./stop-services.sh"
echo "Logs are available in the logs/ directory"

# Keep script running
echo "Press Ctrl+C to stop all services..."
wait

