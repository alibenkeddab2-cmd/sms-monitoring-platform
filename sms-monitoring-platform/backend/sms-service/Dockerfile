FROM openjdk:17-jdk-slim

LABEL maintainer="SMS Platform Team"
LABEL version="1.0.0"
LABEL description="SMS Service for SMS Monitoring Platform"

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create logs directory
RUN mkdir -p logs

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/api/v1/sms/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "target/sms-service-1.0.0.jar"]

