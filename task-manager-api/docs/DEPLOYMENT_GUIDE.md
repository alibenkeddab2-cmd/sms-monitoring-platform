# Task Manager API Deployment Guide

This guide provides comprehensive instructions for deploying the Task Manager API in various environments, from local development to production.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Docker Deployment](#docker-deployment)
4. [Production Deployment](#production-deployment)
5. [Environment Configuration](#environment-configuration)
6. [Database Setup](#database-setup)
7. [Security Configuration](#security-configuration)
8. [Monitoring and Logging](#monitoring-and-logging)
9. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements
- **Java**: OpenJDK 17 or higher
- **Maven**: 3.6.0 or higher
- **PostgreSQL**: 12.0 or higher
- **Docker**: 20.10.0 or higher (for containerized deployment)
- **Docker Compose**: 1.29.0 or higher

### Hardware Requirements

#### Minimum (Development)
- CPU: 2 cores
- RAM: 4 GB
- Storage: 10 GB free space

#### Recommended (Production)
- CPU: 4 cores
- RAM: 8 GB
- Storage: 50 GB free space
- Network: 1 Gbps

## Local Development Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/task-manager-api.git
cd task-manager-api
```

### 2. Database Setup
```bash
# Install PostgreSQL (Ubuntu/Debian)
sudo apt update
sudo apt install postgresql postgresql-contrib

# Start PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database and user
sudo -u postgres psql
CREATE DATABASE taskmanager;
CREATE USER taskuser WITH PASSWORD 'taskpass';
GRANT ALL PRIVILEGES ON DATABASE taskmanager TO taskuser;
\q
```

### 3. Configure Application Properties
Create `src/main/resources/application-dev.yml`:
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:postgresql://localhost:5432/taskmanager
    username: taskuser
    password: taskpass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secret: myDevelopmentSecretKey
  expiration: 86400000

logging:
  level:
    com.taskmanager: DEBUG
```

### 4. Build and Run
```bash
# Build the application
mvn clean compile

# Run tests
mvn test

# Start the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. Verify Installation
```bash
# Check health endpoint
curl http://localhost:8080/actuator/health

# Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

## Docker Deployment

### 1. Using Docker Compose (Recommended)
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### 2. Manual Docker Build
```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t task-manager-api:latest .

# Run PostgreSQL container
docker run -d \
  --name task-manager-postgres \
  -e POSTGRES_DB=taskmanager \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpass \
  -p 5432:5432 \
  postgres:15-alpine

# Run application container
docker run -d \
  --name task-manager-api \
  --link task-manager-postgres:postgres \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/taskmanager \
  -e SPRING_DATASOURCE_USERNAME=taskuser \
  -e SPRING_DATASOURCE_PASSWORD=taskpass \
  -p 8080:8080 \
  task-manager-api:latest
```

### 3. Docker Compose Configuration
The provided `docker-compose.yml` includes:
- PostgreSQL database with persistent storage
- Task Manager API application
- Redis for caching (optional)
- Nginx reverse proxy (optional)

## Production Deployment

### 1. AWS EC2 Deployment

#### Launch EC2 Instance
```bash
# Launch Ubuntu 22.04 LTS instance
# Instance type: t3.medium or larger
# Security groups: Allow ports 22, 80, 443, 8080
```

#### Install Dependencies
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### Deploy Application
```bash
# Clone repository
git clone https://github.com/yourusername/task-manager-api.git
cd task-manager-api

# Set production environment variables
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET=your-production-secret-key-here
export POSTGRES_PASSWORD=your-secure-password

# Deploy with Docker Compose
docker-compose -f docker-compose.prod.yml up -d
```

### 2. Kubernetes Deployment

#### Create Namespace
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: task-manager
```

#### PostgreSQL Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: task-manager
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15-alpine
        env:
        - name: POSTGRES_DB
          value: taskmanager
        - name: POSTGRES_USER
          value: taskuser
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: postgres-secret
              key: password
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: postgres-storage
        persistentVolumeClaim:
          claimName: postgres-pvc
```

#### Application Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: task-manager-api
  namespace: task-manager
spec:
  replicas: 3
  selector:
    matchLabels:
      app: task-manager-api
  template:
    metadata:
      labels:
        app: task-manager-api
    spec:
      containers:
      - name: task-manager-api
        image: task-manager-api:latest
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: prod
        - name: SPRING_DATASOURCE_URL
          value: jdbc:postgresql://postgres:5432/taskmanager
        - name: SPRING_DATASOURCE_USERNAME
          value: taskuser
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: postgres-secret
              key: password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: jwt-secret
              key: secret
        ports:
        - containerPort: 8080
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

### 3. Heroku Deployment

#### Prepare Application
```bash
# Create Procfile
echo "web: java -Dserver.port=\$PORT \$JAVA_OPTS -jar target/task-manager-api-*.jar" > Procfile

# Create system.properties
echo "java.runtime.version=17" > system.properties
```

#### Deploy to Heroku
```bash
# Install Heroku CLI
curl https://cli-assets.heroku.com/install.sh | sh

# Login to Heroku
heroku login

# Create application
heroku create your-task-manager-api

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_SECRET=your-production-secret-key

# Deploy application
git push heroku main

# Scale application
heroku ps:scale web=1
```

## Environment Configuration

### Development Environment
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

jwt:
  secret: devSecretKey
  expiration: 86400000

logging:
  level:
    com.taskmanager: DEBUG
```

### Production Environment
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

logging:
  level:
    com.taskmanager: WARN
  file:
    name: /var/log/task-manager-api/application.log
```

### Environment Variables
```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/taskmanager
DATABASE_USERNAME=taskuser
DATABASE_PASSWORD=secure_password

# JWT Configuration
JWT_SECRET=your-very-long-and-secure-secret-key-here

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Logging Configuration
LOG_LEVEL=INFO
LOG_FILE=/var/log/task-manager-api/application.log
```

## Database Setup

### PostgreSQL Installation and Configuration

#### Ubuntu/Debian
```bash
# Install PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

# Configure PostgreSQL
sudo -u postgres psql
ALTER USER postgres PASSWORD 'secure_password';
CREATE DATABASE taskmanager;
CREATE USER taskuser WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE taskmanager TO taskuser;
\q

# Configure pg_hba.conf for authentication
sudo nano /etc/postgresql/14/main/pg_hba.conf
# Change 'peer' to 'md5' for local connections

# Restart PostgreSQL
sudo systemctl restart postgresql
```

#### CentOS/RHEL
```bash
# Install PostgreSQL
sudo dnf install postgresql postgresql-server postgresql-contrib

# Initialize database
sudo postgresql-setup --initdb

# Start and enable PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Configure database
sudo -u postgres psql
ALTER USER postgres PASSWORD 'secure_password';
CREATE DATABASE taskmanager;
CREATE USER taskuser WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE taskmanager TO taskuser;
\q
```

### Database Migration

#### Initial Setup
```sql
-- Run the initialization script
\i docker/postgres/init.sql
```

#### Backup and Restore
```bash
# Create backup
pg_dump -h localhost -U taskuser -d taskmanager > backup.sql

# Restore backup
psql -h localhost -U taskuser -d taskmanager < backup.sql
```

## Security Configuration

### SSL/TLS Configuration

#### Generate SSL Certificate
```bash
# Generate self-signed certificate (development only)
keytool -genkeypair -alias taskmanager -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 3650

# For production, use Let's Encrypt
sudo certbot --nginx -d yourdomain.com
```

#### Configure HTTPS
```yaml
server:
  port: 8443
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: password
    key-store-type: PKCS12
    key-alias: taskmanager
```

### Firewall Configuration
```bash
# Ubuntu/Debian (UFW)
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
sudo ufw enable

# CentOS/RHEL (firewalld)
sudo firewall-cmd --permanent --add-port=22/tcp
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

### JWT Security Best Practices
```yaml
jwt:
  # Use a strong, randomly generated secret (minimum 256 bits)
  secret: ${JWT_SECRET}
  # Set appropriate expiration time
  expiration: 3600000  # 1 hour
  # Enable token refresh
  refresh-expiration: 604800000  # 7 days
```

## Monitoring and Logging

### Application Monitoring

#### Actuator Endpoints
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

#### Prometheus Configuration
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'task-manager-api'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
```

### Logging Configuration

#### Logback Configuration
```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/var/log/task-manager-api/application.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/var/log/task-manager-api/application.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="FILE" />
        </root>
    </springProfile>
</configuration>
```

#### Log Rotation
```bash
# Create logrotate configuration
sudo nano /etc/logrotate.d/task-manager-api

/var/log/task-manager-api/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 taskmanager taskmanager
    postrotate
        systemctl reload task-manager-api
    endscript
}
```

## Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check Java version
java -version

# Check if port is in use
sudo netstat -tlnp | grep :8080

# Check application logs
tail -f logs/task-manager-api.log
```

#### Database Connection Issues
```bash
# Test database connection
psql -h localhost -U taskuser -d taskmanager

# Check PostgreSQL status
sudo systemctl status postgresql

# Check PostgreSQL logs
sudo tail -f /var/log/postgresql/postgresql-14-main.log
```

#### Memory Issues
```bash
# Check memory usage
free -h

# Adjust JVM memory settings
export JAVA_OPTS="-Xmx1024m -Xms512m"
```

#### Performance Issues
```bash
# Monitor application metrics
curl http://localhost:8080/actuator/metrics

# Check database performance
SELECT * FROM pg_stat_activity;

# Analyze slow queries
SELECT query, mean_time, calls 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;
```

### Health Checks

#### Application Health
```bash
# Basic health check
curl http://localhost:8080/actuator/health

# Detailed health check
curl http://localhost:8080/actuator/health | jq
```

#### Database Health
```bash
# Check database connectivity
pg_isready -h localhost -p 5432

# Check database size
SELECT pg_size_pretty(pg_database_size('taskmanager'));
```

### Backup and Recovery

#### Database Backup
```bash
# Create automated backup script
#!/bin/bash
BACKUP_DIR="/backup/taskmanager"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

pg_dump -h localhost -U taskuser taskmanager > $BACKUP_DIR/taskmanager_$DATE.sql
gzip $BACKUP_DIR/taskmanager_$DATE.sql

# Keep only last 7 days of backups
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete
```

#### Application Backup
```bash
# Backup application files
tar -czf task-manager-api-backup-$(date +%Y%m%d).tar.gz \
  /opt/task-manager-api \
  /etc/systemd/system/task-manager-api.service \
  /var/log/task-manager-api
```

### Performance Tuning

#### JVM Tuning
```bash
# Production JVM settings
export JAVA_OPTS="-Xmx2048m -Xms1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxGCPauseMillis=200"
```

#### Database Tuning
```sql
-- PostgreSQL configuration tuning
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;
SELECT pg_reload_conf();
```

## Support and Maintenance

### Regular Maintenance Tasks
1. **Daily**: Check application logs and health status
2. **Weekly**: Review performance metrics and database statistics
3. **Monthly**: Update dependencies and security patches
4. **Quarterly**: Review and update security configurations

### Monitoring Checklist
- [ ] Application health endpoint responding
- [ ] Database connectivity working
- [ ] Disk space sufficient (>20% free)
- [ ] Memory usage within limits (<80%)
- [ ] CPU usage normal (<70% average)
- [ ] No critical errors in logs
- [ ] SSL certificates valid (>30 days remaining)
- [ ] Backups completing successfully

For additional support, please refer to the project documentation or contact the development team.

