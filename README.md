# Portfolio Projects Summary

## Overview

Two professional projects have been developed, suitable for resumes and job applications in software development. The projects are designed in a professional and clean manner to showcase advanced technical skills.

## Project 1: SMS Monitoring Platform

### Description
A comprehensive platform for monitoring and simulating telecommunication networks with a focus on SMS services. The project utilizes a microservices architecture and provides an advanced monitoring interface.

### Key Features
- **Microservices Architecture**: Microservices architecture with Spring Cloud
- **Real-time Monitoring**: Real-time monitoring of network status and messages
- **Network Simulation**: Simulation of telecommunication networks for performance testing
- **Angular Dashboard**: Advanced user interface with Angular and Material Design
- **Scalable Infrastructure**: Scalable architecture with Docker and Kubernetes

### Technology Stack
**Backend:**
- Java 17
- Spring Boot 3.x
- Spring Cloud (Config Server, Eureka, Gateway)
- MySQL 8.0
- Redis
- Docker & Docker Compose

**Frontend:**
- Angular 16+
- TypeScript
- Angular Material
- Chart.js
- RxJS

**Infrastructure:**
- AWS EC2
- Docker Containerization
- Nginx Load Balancer

### Project Structure
```
sms-monitoring-platform/
├── backend/
│   ├── config-server/          # Configuration management
│   ├── eureka-server/          # Service discovery
│   ├── gateway-service/        # API Gateway
│   ├── sms-service/           # Core SMS processing
│   ├── notification-service/   # Real-time notifications
│   └── monitoring-service/     # System monitoring
├── frontend/
│   └── sms-dashboard/         # Angular dashboard
├── docker/                    # Docker configurations
├── docs/                      # Documentation
└── README.md
```

### Key Achievements
- Implemented complete microservices ecosystem
- Built real-time monitoring dashboard
- Created scalable network simulation engine
- Integrated comprehensive logging and monitoring
- Designed responsive Angular frontend

---

## Project 2: Task Manager API

### Description
A comprehensive task management API with a JWT authentication system and advanced authorization. The project provides all essential operations for managing tasks and users.

### Key Features
- **RESTful API Design**: REST-compliant API design
- **JWT Authentication**: Secure authentication system using JWT
- **Role-based Access Control**: Advanced authorization system
- **Comprehensive CRUD Operations**: Full operations for tasks and users
- **Advanced Search & Filtering**: Advanced search and filtering
- **API Documentation**: Interactive documentation with Swagger

### Technology Stack
**Backend:**
- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- Maven

**Documentation & Testing:**
- Swagger/OpenAPI 3
- JUnit 5
- Testcontainers
- Postman Collections

**Deployment:**
- Docker & Docker Compose
- AWS EC2
- Heroku Ready

### Project Structure
```
task-manager-api/
├── src/
│   ├── main/java/com/taskmanager/
│   │   ├── controller/         # REST Controllers
│   │   ├── service/           # Business Logic
│   │   ├── repository/        # Data Access Layer
│   │   ├── model/             # Entity Classes
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── config/            # Configuration
│   │   ├── security/          # Security Components
│   │   └── exception/         # Exception Handling
│   └── resources/
│       ├── application.yml    # Configuration
│       └── data.sql          # Sample Data
├── docs/                      # API Documentation
├── docker/                    # Docker configurations
└── README.md
```

### API Endpoints
**Authentication:**
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Token refresh

**Task Management:**
- `GET /api/tasks` - Get user tasks (paginated)
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PATCH /api/tasks/{id}/status` - Update task status

**User Management:**
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update profile
- `GET /api/users` - Get all users (Admin only)

### Key Achievements
- Implemented secure JWT authentication system
- Built comprehensive REST API with full CRUD operations
- Created role-based authorization system
- Integrated advanced search and filtering capabilities
- Developed comprehensive API documentation
- Implemented proper error handling and validation

---

## Technical Highlights

### Architecture Patterns
- **Microservices Architecture** (SMS Platform)
- **Layered Architecture** (Task Manager API)
- **Repository Pattern** for data access
- **DTO Pattern** for data transfer
- **Factory Pattern** for object creation

### Security Implementation
- **JWT Token-based Authentication**
- **Role-based Access Control (RBAC)**
- **Password Encryption** with BCrypt
- **CORS Configuration** for cross-origin requests
- **Input Validation** and sanitization

### Database Design
- **Normalized Database Schema**
- **Proper Indexing** for performance
- **Foreign Key Constraints**
- **Audit Fields** (created_at, updated_at)
- **Database Migrations** support

### DevOps & Deployment
- **Docker Containerization**
- **Docker Compose** for multi-service orchestration
- **Environment-specific Configurations**
- **Health Checks** and monitoring
- **Logging** and error tracking

### Code Quality
- **Clean Code Principles**
- **SOLID Principles** implementation
- **Comprehensive Documentation**
- **Unit and Integration Testing**
- **Code Organization** and structure

---

## Resume Integration

### Skills Demonstrated

**Programming Languages:**
- Java 17 (Advanced)
- TypeScript/JavaScript (Intermediate)
- SQL (Advanced)

**Frameworks & Libraries:**
- Spring Boot 3.x
- Spring Cloud
- Spring Security
- Spring Data JPA
- Angular 16+
- Angular Material

**Databases:**
- MySQL 8.0
- PostgreSQL
- Redis

**Tools & Technologies:**
- Docker & Docker Compose
- Maven
- Git & GitHub
- Swagger/OpenAPI
- JWT Authentication
- RESTful API Design

**Cloud & DevOps:**
- AWS EC2
- Docker Containerization
- Microservices Architecture
- CI/CD Concepts

### Project Descriptions for Resume

**SMS Monitoring Platform (Personal Project)**
- Developed a comprehensive microservices-based SMS monitoring platform using Java 17, Spring Cloud, and Angular
- Implemented real-time network simulation and monitoring with MySQL database and Redis caching
- Built scalable architecture with Docker containerization and deployed on AWS EC2
- Created responsive Angular dashboard with real-time data visualization and Material Design

**Task Manager API (Personal Project)**
- Designed and developed a RESTful API for task management using Java 17, Spring Boot, and PostgreSQL
- Implemented JWT-based authentication and role-based authorization system
- Built comprehensive CRUD operations with advanced search, filtering, and pagination
- Created detailed API documentation with Swagger and deployed using Docker

---

## Getting Started

### SMS Monitoring Platform
```bash
# Clone the repository
git clone <repository-url>
cd sms-monitoring-platform

# Start with Docker Compose
docker-compose up -d

# Access the dashboard
open http://localhost:4200
```

### Task Manager API
```bash
# Clone the repository
git clone <repository-url>
cd task-manager-api

# Start with Docker Compose
docker-compose up -d

# Access API documentation
open http://localhost:8080/swagger-ui.html
```

---

## Documentation

### SMS Monitoring Platform
- **Architecture Documentation**: `/docs/ARCHITECTURE.md`
- **API Documentation**: Available in Swagger UI
- **Deployment Guide**: `/docs/DEPLOYMENT.md`

### Task Manager API
- **API Documentation**: `/docs/API_DOCUMENTATION.md`
- **Deployment Guide**: `/docs/DEPLOYMENT_GUIDE.md`
- **Interactive API Docs**: Swagger UI at `/swagger-ui.html`

---

## Contact Information

For questions about these projects or technical discussions, please feel free to reach out.

ali benkeddab/ kingtech/
e-mail/  alibenkeddab2@gmail.com
whatsapp/+213671491709
