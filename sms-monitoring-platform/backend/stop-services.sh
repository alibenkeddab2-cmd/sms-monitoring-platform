#!/bin/bash

# SMS Monitoring Platform - Service Stop Script
# This script stops all backend services

echo "Stopping SMS Monitoring Platform Services..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if PID file exists
if [ ! -f "service-pids.txt" ]; then
    echo -e "${YELLOW}No service PID file found. Attempting to find and stop services...${NC}"
    
    # Try to find and kill services by port
    echo "Stopping services by port..."
    
    # Kill processes on known ports
    for port in 8888 8761 8080 8081 8082 8083; do
        PID=$(lsof -ti:$port)
        if [ ! -z "$PID" ]; then
            echo "Killing process on port $port (PID: $PID)"
            kill -9 $PID 2>/dev/null
        fi
    done
    
    echo -e "${GREEN}Service cleanup completed.${NC}"
    exit 0
fi

# Read PIDs from file and stop services
echo "Reading service PIDs from file..."
source service-pids.txt

# Function to stop service
stop_service() {
    local service_name=$1
    local pid=$2
    
    if [ ! -z "$pid" ] && kill -0 $pid 2>/dev/null; then
        echo -e "${YELLOW}Stopping $service_name (PID: $pid)...${NC}"
        kill $pid
        
        # Wait for graceful shutdown
        sleep 3
        
        # Force kill if still running
        if kill -0 $pid 2>/dev/null; then
            echo -e "${YELLOW}Force stopping $service_name...${NC}"
            kill -9 $pid
        fi
        
        echo -e "${GREEN}$service_name stopped.${NC}"
    else
        echo -e "${YELLOW}$service_name is not running or PID not found.${NC}"
    fi
}

# Stop services in reverse order
stop_service "Monitoring Service" $MONITORING_PID
stop_service "Notification Service" $NOTIFICATION_PID
stop_service "SMS Service" $SMS_PID
stop_service "Gateway Service" $GATEWAY_PID
stop_service "Eureka Server" $EUREKA_PID
stop_service "Config Server" $CONFIG_PID

# Clean up PID file
rm -f service-pids.txt

echo -e "${GREEN}"
echo "=========================================="
echo "All services stopped successfully!"
echo "=========================================="
echo -e "${NC}"

