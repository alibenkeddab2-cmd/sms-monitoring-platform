# Task Manager API Documentation

## Overview

The Task Manager API is a comprehensive RESTful web service built with Java 17, Spring Boot 3.x, and PostgreSQL. It provides a complete task management solution with JWT-based authentication, role-based access control, and extensive CRUD operations for both users and tasks.

## Base URL

```
http://localhost:8080/api
```

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Response Format

All API responses follow a consistent JSON format:

### Success Response
```json
{
  "data": { ... },
  "message": "Success message",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Response
```json
{
  "error": "Error type",
  "message": "Error description",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/api/endpoint"
}
```

## Authentication Endpoints

### POST /auth/register
Register a new user account.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "enabled": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### POST /auth/login
Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "usernameOrEmail": "john_doe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

### POST /auth/refresh
Refresh JWT token if close to expiration.

**Headers:**
```
Authorization: Bearer <current-token>
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

### GET /auth/check-username
Check if username is available for registration.

**Query Parameters:**
- `username` (string, required): Username to check

**Response:**
```json
true
```

### GET /auth/check-email
Check if email is available for registration.

**Query Parameters:**
- `email` (string, required): Email to check

**Response:**
```json
true
```

## User Management Endpoints

### GET /users/profile
Get current user's profile information.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "enabled": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### PUT /users/profile
Update current user's profile information.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "username": "john_doe_updated",
  "email": "john.updated@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "john_doe_updated",
  "email": "john.updated@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "enabled": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T11:00:00Z"
}
```

### GET /users (Admin Only)
Get all users with pagination.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Query Parameters:**
- `page` (integer, optional): Page number (0-based), default: 0
- `size` (integer, optional): Page size, default: 20
- `sortBy` (string, optional): Sort field, default: "createdAt"
- `sortDir` (string, optional): Sort direction ("asc" or "desc"), default: "desc"

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "USER",
      "enabled": true,
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 1
}
```

### GET /users/{id} (Admin Only)
Get user by ID.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Path Parameters:**
- `id` (integer, required): User ID

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "enabled": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### GET /users/search (Admin Only)
Search users by name or username.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Query Parameters:**
- `q` (string, required): Search term
- `page` (integer, optional): Page number (0-based), default: 0
- `size` (integer, optional): Page size, default: 20

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "USER",
      "enabled": true,
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 1
}
```

### PUT /users/{id}/role (Admin Only)
Update user's role.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Path Parameters:**
- `id` (integer, required): User ID

**Query Parameters:**
- `role` (string, required): New role ("USER" or "ADMIN")

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "ADMIN",
  "enabled": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T11:00:00Z"
}
```

### PUT /users/{id}/status (Admin Only)
Toggle user's enabled status.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Path Parameters:**
- `id` (integer, required): User ID

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "enabled": false,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T11:00:00Z"
}
```

### DELETE /users/{id} (Admin Only)
Delete user account.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Path Parameters:**
- `id` (integer, required): User ID

**Response:**
```
204 No Content
```

## Task Management Endpoints

### POST /tasks
Create a new task.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the task manager API",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59"
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the task manager API",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "completedAt": null,
  "userId": 1,
  "userFullName": "John Doe",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### GET /tasks/{id}
Get task by ID.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (integer, required): Task ID

**Response:**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the task manager API",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "completedAt": null,
  "userId": 1,
  "userFullName": "John Doe",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### GET /tasks
Get current user's tasks with pagination.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `page` (integer, optional): Page number (0-based), default: 0
- `size` (integer, optional): Page size, default: 20
- `sortBy` (string, optional): Sort field, default: "createdAt"
- `sortDir` (string, optional): Sort direction ("asc" or "desc"), default: "desc"

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Complete project documentation",
      "description": "Write comprehensive documentation for the task manager API",
      "status": "TODO",
      "priority": "HIGH",
      "dueDate": "2024-12-31T23:59:59",
      "completedAt": null,
      "userId": 1,
      "userFullName": "John Doe",
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 1
}
```

### GET /tasks/all (Admin Only)
Get all tasks with pagination.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Query Parameters:**
- `page` (integer, optional): Page number (0-based), default: 0
- `size` (integer, optional): Page size, default: 20
- `sortBy` (string, optional): Sort field, default: "createdAt"
- `sortDir` (string, optional): Sort direction ("asc" or "desc"), default: "desc"

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Complete project documentation",
      "description": "Write comprehensive documentation for the task manager API",
      "status": "TODO",
      "priority": "HIGH",
      "dueDate": "2024-12-31T23:59:59",
      "completedAt": null,
      "userId": 1,
      "userFullName": "John Doe",
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 1
}
```

### GET /tasks/status/{status}
Get current user's tasks by status.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `status` (string, required): Task status ("TODO", "IN_PROGRESS", or "DONE")

**Response:**
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the task manager API",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59",
    "completedAt": null,
    "userId": 1,
    "userFullName": "John Doe",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
]
```

### GET /tasks/search
Search current user's tasks by title or description.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `q` (string, required): Search term
- `page` (integer, optional): Page number (0-based), default: 0
- `size` (integer, optional): Page size, default: 20

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Complete project documentation",
      "description": "Write comprehensive documentation for the task manager API",
      "status": "TODO",
      "priority": "HIGH",
      "dueDate": "2024-12-31T23:59:59",
      "completedAt": null,
      "userId": 1,
      "userFullName": "John Doe",
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 1
}
```

### GET /tasks/overdue
Get current user's overdue tasks.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the task manager API",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-01-10T23:59:59",
    "completedAt": null,
    "userId": 1,
    "userFullName": "John Doe",
    "createdAt": "2024-01-05T10:30:00Z",
    "updatedAt": "2024-01-05T10:30:00Z"
  }
]
```

### GET /tasks/due-soon
Get current user's tasks due within specified hours.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `hours` (integer, optional): Hours ahead to check, default: 24

**Response:**
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the task manager API",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-01-16T10:30:00Z",
    "completedAt": null,
    "userId": 1,
    "userFullName": "John Doe",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
]
```

### GET /tasks/completed
Get current user's recently completed tasks.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `days` (integer, optional): Days back to check, default: 7
- `limit` (integer, optional): Maximum number of tasks, default: 10

**Response:**
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the task manager API",
    "status": "DONE",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59",
    "completedAt": "2024-01-15T15:30:00Z",
    "userId": 1,
    "userFullName": "John Doe",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T15:30:00Z"
  }
]
```

### PUT /tasks/{id}
Update an existing task.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (integer, required): Task ID

**Request Body:**
```json
{
  "title": "Complete project documentation - Updated",
  "description": "Write comprehensive documentation for the task manager API with examples",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59"
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Complete project documentation - Updated",
  "description": "Write comprehensive documentation for the task manager API with examples",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "completedAt": null,
  "userId": 1,
  "userFullName": "John Doe",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T14:00:00Z"
}
```

### PATCH /tasks/{id}/status
Update the status of a task.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (integer, required): Task ID

**Query Parameters:**
- `status` (string, required): New status ("TODO", "IN_PROGRESS", or "DONE")

**Response:**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the task manager API",
  "status": "DONE",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "completedAt": "2024-01-15T15:30:00Z",
  "userId": 1,
  "userFullName": "John Doe",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T15:30:00Z"
}
```

### DELETE /tasks/{id}
Delete a task.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (integer, required): Task ID

**Response:**
```
204 No Content
```

### GET /tasks/statistics
Get current user's task statistics.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "totalTasks": 10,
  "todoTasks": 3,
  "inProgressTasks": 2,
  "completedTasks": 4,
  "overdueTasks": 1,
  "completionRate": 40.0
}
```

### GET /tasks/statistics/overall (Admin Only)
Get overall task statistics.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "totalTasks": 100,
  "todoTasks": 30,
  "inProgressTasks": 20,
  "completedTasks": 45,
  "overdueTasks": 5,
  "completionRate": 45.0
}
```

## Data Models

### User
```json
{
  "id": "integer",
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "role": "USER | ADMIN",
  "enabled": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### Task
```json
{
  "id": "integer",
  "title": "string",
  "description": "string",
  "status": "TODO | IN_PROGRESS | DONE",
  "priority": "LOW | MEDIUM | HIGH | URGENT",
  "dueDate": "datetime",
  "completedAt": "datetime",
  "userId": "integer",
  "userFullName": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

## Error Codes

| HTTP Status | Error Code | Description |
|-------------|------------|-------------|
| 400 | BAD_REQUEST | Invalid request data |
| 401 | UNAUTHORIZED | Authentication required |
| 403 | FORBIDDEN | Insufficient permissions |
| 404 | NOT_FOUND | Resource not found |
| 409 | CONFLICT | Resource already exists |
| 422 | UNPROCESSABLE_ENTITY | Validation error |
| 500 | INTERNAL_SERVER_ERROR | Server error |

## Rate Limiting

The API implements rate limiting to prevent abuse:
- 100 requests per minute per IP address
- 1000 requests per hour per authenticated user

## Pagination

All paginated endpoints support the following query parameters:
- `page`: Page number (0-based), default: 0
- `size`: Page size, default: 20, max: 100
- `sortBy`: Sort field, default varies by endpoint
- `sortDir`: Sort direction ("asc" or "desc"), default: "desc"

## Filtering and Searching

Search endpoints support partial matching and are case-insensitive. Multiple search terms are treated as AND conditions.

## Swagger Documentation

Interactive API documentation is available at:
```
http://localhost:8080/swagger-ui.html
```

OpenAPI specification is available at:
```
http://localhost:8080/v3/api-docs
```

