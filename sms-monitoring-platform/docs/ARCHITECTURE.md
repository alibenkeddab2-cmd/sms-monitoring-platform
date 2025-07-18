# SMS Monitoring Platform - Architecture Documentation

## Overview

The SMS Monitoring Platform is a comprehensive microservices-based system designed to simulate and monitor telecom network operations. Built with modern technologies including Java 17, Spring Cloud, Docker, MySQL, and Angular, the platform provides real-time monitoring, analytics, and management capabilities for SMS traffic simulation.

## System Architecture

### High-Level Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular UI    │    │   Gateway       │    │   Config        │
│   Dashboard     │◄──►│   Service       │◄──►│   Server        │
│   (Port 4200)   │    │   (Port 8080)   │    │   (Port 8888)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Eureka        │    │   SMS Service   │    │   Notification  │
│   Server        │◄──►│   (Port 8081)   │◄──►│   Service       │
│   (Port 8761)   │    │                 │    │   (Port 8082)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   MySQL         │    │   Monitoring    │    │   Redis         │
│   Database      │◄──►│   Service       │◄──►│   Cache         │
│   (Port 3306)   │    │   (Port 8083)   │    │   (Port 6379)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Microservices Architecture

The platform follows a microservices architecture pattern with the following core services:

#### 1. Config Server (Port 8888)
- **Purpose**: Centralized configuration management
- **Technology**: Spring Cloud Config
- **Responsibilities**:
  - Store and serve configuration for all microservices
  - Support for environment-specific configurations
  - Configuration versioning and rollback capabilities
  - Integration with Git repositories for configuration storage

#### 2. Eureka Server (Port 8761)
- **Purpose**: Service discovery and registration
- **Technology**: Netflix Eureka
- **Responsibilities**:
  - Service registration and discovery
  - Health monitoring of registered services
  - Load balancing support
  - Failover and redundancy management

#### 3. Gateway Service (Port 8080)
- **Purpose**: API Gateway and routing
- **Technology**: Spring Cloud Gateway
- **Responsibilities**:
  - Request routing to appropriate microservices
  - Authentication and authorization
  - Rate limiting and throttling
  - Request/response transformation
  - CORS handling

#### 4. SMS Service (Port 8081)
- **Purpose**: Core SMS processing and simulation
- **Technology**: Spring Boot, JPA/Hibernate
- **Responsibilities**:
  - SMS message processing and validation
  - Network simulation logic
  - Operator management
  - Message status tracking
  - Performance metrics collection

#### 5. Notification Service (Port 8082)
- **Purpose**: Real-time notifications and alerts
- **Technology**: Spring Boot, WebSocket
- **Responsibilities**:
  - Real-time event notifications
  - Alert management
  - Email and SMS notifications
  - WebSocket connections for live updates

#### 6. Monitoring Service (Port 8083)
- **Purpose**: System monitoring and analytics
- **Technology**: Spring Boot, Micrometer
- **Responsibilities**:
  - Performance metrics collection
  - System health monitoring
  - Analytics and reporting
  - Log aggregation

## Technology Stack

### Backend Technologies

#### Core Framework
- **Java 17**: Latest LTS version with modern language features
- **Spring Boot 3.x**: Application framework with auto-configuration
- **Spring Cloud 2023.x**: Microservices infrastructure components
- **Maven 3.9+**: Dependency management and build tool

#### Microservices Components
- **Spring Cloud Config**: Centralized configuration management
- **Netflix Eureka**: Service discovery and registration
- **Spring Cloud Gateway**: API gateway and routing
- **Spring Cloud LoadBalancer**: Client-side load balancing
- **Spring Cloud Circuit Breaker**: Fault tolerance patterns

#### Data Layer
- **Spring Data JPA**: Data access abstraction
- **Hibernate**: ORM framework
- **MySQL 8.0**: Primary relational database
- **Redis 7.x**: Caching and session storage
- **HikariCP**: High-performance connection pooling

#### Monitoring and Observability
- **Micrometer**: Application metrics
- **Spring Boot Actuator**: Production-ready features
- **Logback**: Logging framework
- **Zipkin**: Distributed tracing (optional)

### Frontend Technologies

#### Core Framework
- **Angular 16+**: Modern web application framework
- **TypeScript 5.x**: Strongly typed JavaScript
- **RxJS**: Reactive programming library
- **Angular CLI**: Development and build tools

#### UI Components
- **Angular Material**: Material Design components
- **Chart.js**: Data visualization
- **Socket.IO Client**: Real-time communication
- **Bootstrap**: Responsive design framework

#### Build and Development
- **Node.js 18+**: JavaScript runtime
- **npm/yarn**: Package management
- **Webpack**: Module bundling
- **ESLint**: Code linting
- **Prettier**: Code formatting

### Infrastructure Technologies

#### Containerization
- **Docker**: Application containerization
- **Docker Compose**: Multi-container orchestration
- **Kubernetes**: Container orchestration (production)

#### Database
- **MySQL 8.0**: Primary database
- **Redis**: Caching and session storage
- **Database migrations**: Flyway/Liquibase

#### Deployment
- **AWS EC2**: Cloud computing instances
- **AWS RDS**: Managed database service
- **AWS ElastiCache**: Managed Redis service
- **Nginx**: Reverse proxy and load balancer

## Data Architecture

### Database Design

#### Core Entities

```sql
-- SMS Messages Table
CREATE TABLE sms_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    message_id VARCHAR(255) UNIQUE NOT NULL,
    sender_number VARCHAR(20) NOT NULL,
    receiver_number VARCHAR(20) NOT NULL,
    message_content TEXT NOT NULL,
    status ENUM('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'EXPIRED') NOT NULL,
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL',
    operator_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    failed_at TIMESTAMP NULL,
    retry_count INT DEFAULT 0,
    error_message TEXT NULL,
    INDEX idx_status (status),
    INDEX idx_operator (operator_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (operator_id) REFERENCES operators(id)
);

-- Operators Table
CREATE TABLE operators (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) UNIQUE NOT NULL,
    country VARCHAR(50) NOT NULL,
    success_rate DECIMAL(5,2) DEFAULT 95.00,
    average_delay_ms INT DEFAULT 1000,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Network Tests Table
CREATE TABLE network_tests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    test_name VARCHAR(255) NOT NULL,
    test_type ENUM('THROUGHPUT', 'LATENCY', 'SUCCESS_RATE', 'LOAD') NOT NULL,
    operator_id BIGINT NOT NULL,
    status ENUM('PENDING', 'RUNNING', 'COMPLETED', 'FAILED') NOT NULL,
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    results JSON NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (operator_id) REFERENCES operators(id)
);
```

#### Data Relationships

```
Operators (1) ──── (N) SMS_Messages
Operators (1) ──── (N) Network_Tests
SMS_Messages (N) ──── (1) Message_Status_History
Network_Tests (1) ──── (N) Test_Results
```

### Caching Strategy

#### Redis Cache Layers

1. **Application Cache**
   - Operator configurations
   - System settings
   - User sessions

2. **Data Cache**
   - Frequently accessed SMS messages
   - Operator statistics
   - Dashboard metrics

3. **Session Cache**
   - User authentication tokens
   - WebSocket connections
   - Temporary data

## Security Architecture

### Authentication and Authorization

#### JWT-Based Authentication
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .build();
    }
}
```

#### Role-Based Access Control
- **ADMIN**: Full system access
- **OPERATOR**: SMS management and monitoring
- **VIEWER**: Read-only access to dashboards

### Network Security

#### API Gateway Security
- Rate limiting and throttling
- Request validation and sanitization
- CORS configuration
- SSL/TLS termination

#### Service-to-Service Communication
- mTLS for internal communication
- Service mesh integration (Istio)
- Network policies and segmentation

## Performance Architecture

### Scalability Patterns

#### Horizontal Scaling
```yaml
# Kubernetes Deployment Example
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sms-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: sms-service
  template:
    spec:
      containers:
      - name: sms-service
        image: sms-service:latest
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
```

#### Load Balancing
- Client-side load balancing with Ribbon
- Server-side load balancing with Nginx
- Database connection pooling

#### Caching Strategy
- Multi-level caching (L1: Application, L2: Redis)
- Cache-aside pattern
- Write-through caching for critical data

### Performance Monitoring

#### Metrics Collection
```java
@Component
public class SmsMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter smsProcessedCounter;
    private final Timer smsProcessingTimer;
    
    public SmsMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.smsProcessedCounter = Counter.builder("sms.processed")
            .description("Number of SMS messages processed")
            .register(meterRegistry);
        this.smsProcessingTimer = Timer.builder("sms.processing.time")
            .description("SMS processing time")
            .register(meterRegistry);
    }
}
```

## Deployment Architecture

### Container Orchestration

#### Docker Compose (Development)
```yaml
version: '3.8'
services:
  config-server:
    build: ./backend/config-server
    ports:
      - "8888:8888"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    
  eureka-server:
    build: ./backend/eureka-server
    ports:
      - "8761:8761"
    depends_on:
      - config-server
    
  sms-service:
    build: ./backend/sms-service
    ports:
      - "8081:8081"
    depends_on:
      - config-server
      - eureka-server
      - mysql
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/sms_platform
```

#### Kubernetes (Production)
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: sms-platform-config
data:
  application.yml: |
    spring:
      profiles:
        active: kubernetes
    eureka:
      client:
        service-url:
          defaultZone: http://eureka-server:8761/eureka/
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sms-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: sms-service
  template:
    metadata:
      labels:
        app: sms-service
    spec:
      containers:
      - name: sms-service
        image: sms-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        volumeMounts:
        - name: config-volume
          mountPath: /config
      volumes:
      - name: config-volume
        configMap:
          name: sms-platform-config
```

### CI/CD Pipeline

#### Build Pipeline
```yaml
# .github/workflows/build.yml
name: Build and Test
on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run tests
      run: mvn clean test
    - name: Build application
      run: mvn clean package -DskipTests
    - name: Build Docker images
      run: docker-compose build
```

#### Deployment Pipeline
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production
on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - name: Deploy to Kubernetes
      run: |
        kubectl apply -f k8s/
        kubectl rollout status deployment/sms-service
```

## Monitoring and Observability

### Application Monitoring

#### Health Checks
```java
@Component
public class SmsServiceHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // Check database connectivity
        // Check external service availability
        // Check message queue status
        
        return Health.up()
            .withDetail("database", "UP")
            .withDetail("messageQueue", "UP")
            .build();
    }
}
```

#### Metrics Dashboard
- **Grafana**: Visualization and alerting
- **Prometheus**: Metrics collection and storage
- **Jaeger**: Distributed tracing
- **ELK Stack**: Log aggregation and analysis

### Alerting Strategy

#### Alert Rules
```yaml
# Prometheus Alert Rules
groups:
- name: sms-platform-alerts
  rules:
  - alert: HighErrorRate
    expr: rate(sms_processing_errors_total[5m]) > 0.1
    for: 2m
    labels:
      severity: warning
    annotations:
      summary: "High SMS processing error rate"
      
  - alert: ServiceDown
    expr: up{job="sms-service"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "SMS Service is down"
```

## Development Guidelines

### Code Organization

#### Package Structure
```
src/main/java/com/smsplatform/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── service/         # Business logic
├── repository/      # Data access layer
├── model/           # Entity classes
├── dto/             # Data transfer objects
├── exception/       # Custom exceptions
├── util/            # Utility classes
└── SmsApplication.java
```

#### Naming Conventions
- **Classes**: PascalCase (e.g., `SmsMessageService`)
- **Methods**: camelCase (e.g., `processSmsMessage`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Packages**: lowercase (e.g., `com.smsplatform.service`)

### Testing Strategy

#### Test Pyramid
```
    ┌─────────────────┐
    │   E2E Tests     │  ← Few, slow, expensive
    │   (Selenium)    │
    ├─────────────────┤
    │ Integration     │  ← Some, medium speed
    │ Tests (Spring)  │
    ├─────────────────┤
    │   Unit Tests    │  ← Many, fast, cheap
    │   (JUnit)       │
    └─────────────────┘
```

#### Test Configuration
```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SmsServiceIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldProcessSmsMessage() {
        // Given
        SmsMessageDto smsDto = new SmsMessageDto();
        smsDto.setSender("1234567890");
        smsDto.setReceiver("0987654321");
        smsDto.setContent("Test message");
        
        // When
        ResponseEntity<SmsMessageDto> response = restTemplate
            .postForEntity("/api/sms", smsDto, SmsMessageDto.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatus()).isEqualTo(SmsStatus.PENDING);
    }
}
```

## Future Enhancements

### Planned Features
1. **Machine Learning Integration**
   - Predictive analytics for network performance
   - Anomaly detection in SMS traffic
   - Intelligent routing optimization

2. **Advanced Monitoring**
   - Real-time network topology visualization
   - Predictive maintenance alerts
   - Performance trend analysis

3. **Multi-tenancy Support**
   - Tenant isolation
   - Resource quotas
   - Billing integration

4. **API Versioning**
   - Backward compatibility
   - Gradual migration support
   - Version-specific documentation

### Technology Roadmap
- **Spring Boot 4.x**: Framework upgrades
- **Java 21**: Latest LTS adoption
- **Kubernetes Native**: Cloud-native optimizations
- **GraphQL**: Alternative API layer
- **Event Sourcing**: Advanced data patterns

## Conclusion

The SMS Monitoring Platform represents a modern, scalable, and maintainable microservices architecture designed for telecom network simulation and monitoring. The architecture emphasizes:

- **Scalability**: Horizontal scaling capabilities
- **Reliability**: Fault tolerance and resilience patterns
- **Maintainability**: Clean code and modular design
- **Observability**: Comprehensive monitoring and logging
- **Security**: Multi-layered security approach

This architecture provides a solid foundation for current requirements while maintaining flexibility for future enhancements and scaling needs.

