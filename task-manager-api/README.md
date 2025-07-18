# Task Manager API

A comprehensive RESTful API for task management with JWT authentication, built with Java, Spring Boot, Spring Security, and PostgreSQL.

## Features

- **User Authentication & Authorization**: JWT-based authentication with role-based access control
- **Task Management**: Complete CRUD operations for tasks with status tracking
- **User Management**: User registration, profile management, and role assignment
- **Security**: Secure endpoints with JWT tokens and password encryption
- **Database Integration**: PostgreSQL database with JPA/Hibernate
- **API Documentation**: Swagger/OpenAPI integration for interactive API documentation
- **Validation**: Comprehensive input validation and error handling
- **Logging**: Structured logging with different log levels
- **Testing**: Unit and integration tests with high code coverage

## Technology Stack

- **Java 17**: Latest LTS version with modern language features
- **Spring Boot 3.x**: Framework for building production-ready applications
- **Spring Security**: Authentication and authorization framework
- **Spring Data JPA**: Data access layer with Hibernate
- **PostgreSQL**: Robust relational database
- **JWT (JSON Web Tokens)**: Stateless authentication mechanism
- **Maven**: Dependency management and build tool
- **Swagger/OpenAPI**: API documentation and testing
- **JUnit 5**: Testing framework
- **Docker**: Containerization support

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token

### Tasks
- `GET /api/tasks` - Get all tasks (paginated)
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PATCH /api/tasks/{id}/status` - Update task status
- `GET /api/tasks/user/{userId}` - Get tasks by user
- `GET /api/tasks/status/{status}` - Get tasks by status

### Users
- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile` - Update user profile
- `GET /api/users/{id}` - Get user by ID (Admin only)
- `GET /api/users` - Get all users (Admin only)

## Quick Start

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Database Setup
1. Create PostgreSQL database:
```sql
CREATE DATABASE taskmanager;
CREATE USER taskuser WITH PASSWORD 'taskpass';
GRANT ALL PRIVILEGES ON DATABASE taskmanager TO taskuser;
```

### Running the Application

1. Clone the repository
```bash
git clone https://github.com/yourusername/task-manager-api.git
cd task-manager-api
```

2. Configure database connection in `application.yml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskmanager
    username: taskuser
    password: taskpass
```

3. Run the application
```bash
mvn spring-boot:run
```

4. Access the API documentation at `http://localhost:8080/swagger-ui.html`

### Using Docker

1. Build and run with Docker Compose
```bash
docker-compose up -d
```

This will start both the application and PostgreSQL database.

## Configuration

### Application Properties
Key configuration options in `application.yml`:

```yaml
jwt:
  secret: your-secret-key
  expiration: 86400000 # 24 hours

spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

### Security Configuration
- JWT token expiration: 24 hours
- Password encryption: BCrypt
- CORS enabled for frontend integration
- Role-based access control (USER, ADMIN)

## API Usage Examples

### User Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securePassword123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### User Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePassword123"
  }'
```

### Create Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the task manager API",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59"
  }'
```

## Testing

Run tests with Maven:
```bash
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report
```

## Project Structure

```
src/
├── main/
│   ├── java/com/taskmanager/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access layer
│   │   ├── model/          # Entity classes
│   │   ├── dto/            # Data transfer objects
│   │   ├── config/         # Configuration classes
│   │   ├── security/       # Security configuration
│   │   └── exception/      # Exception handling
│   └── resources/
│       ├── application.yml # Application configuration
│       └── data.sql        # Initial data
└── test/
    └── java/com/taskmanager/ # Test classes
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For questions or support, please contact alibenkeddab2@gmail.com +213671491709

