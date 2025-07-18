# SMS Monitoring Platform

A comprehensive microservices-based platform for monitoring and testing telecom network SMS services. Built with modern technologies including Java 17, Spring Cloud, Docker, MySQL, and Angular.

## Architecture Overview

The platform follows a microservices architecture pattern with the following components:

- **Config Server**: Centralized configuration management
- **Eureka Server**: Service discovery and registration
- **Gateway Service**: API gateway for routing and load balancing
- **SMS Service**: Core SMS processing and simulation
- **Notification Service**: Real-time notifications and alerts
- **Monitoring Service**: System health monitoring and metrics
- **Frontend Dashboard**: Angular-based web interface

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.x
- Spring Cloud 2023.x
- Spring Security
- MySQL 8.0
- Docker & Docker Compose
- Maven

### Frontend
- Angular 16+
- TypeScript
- Bootstrap 5
- Chart.js for data visualization
- WebSocket for real-time updates

### Infrastructure
- AWS EC2 for deployment
- Docker containers
- MySQL database
- Redis for caching

## Features

- **Real-time SMS Monitoring**: Track SMS delivery status and performance metrics
- **Network Simulation**: Simulate various telecom network conditions
- **Performance Analytics**: Comprehensive dashboards with charts and reports
- **Alert System**: Configurable alerts for system anomalies
- **Multi-tenant Support**: Support for multiple telecom operators
- **API Documentation**: Swagger/OpenAPI integration
- **Security**: JWT-based authentication and authorization

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- MySQL 8.0

### Running the Application

1. Clone the repository
```bash
git clone https://github.com/yourusername/sms-monitoring-platform.git
cd sms-monitoring-platform
```

2. Start the infrastructure services
```bash
docker-compose up -d mysql redis
```

3. Start the backend services
```bash
cd backend
./start-services.sh
```

4. Start the frontend application
```bash
cd frontend/sms-dashboard
npm install
npm start
```

5. Access the application at `http://localhost:4200`

## API Documentation

Once the services are running, you can access the API documentation at:
- Gateway API: `http://localhost:8080/swagger-ui.html`
- Individual service docs available through the gateway

## Monitoring & Health Checks

- Health checks: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Service registry: `http://localhost:8761`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please contact alibenkeddab2@gmail.com 
+213671491709

