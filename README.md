# HR Microservices Assessment

This project is a microservices-based HR system composed of multiple services and infrastructure components orchestrated using Docker.

---

## Project Structure

```
hr-microservices-assassement
├── infra             # Docker infrastructure (grafana, Kafka, services orchestration)
├── services   
           ├── employee-service   
           └── leave-service
└── collection        # API collection (Postman / testing requests)
```

---

## Tech Stack

- Java / Spring Boot
- Maven
- PostgreSQL
- Kafka
- Observability
- Docker & Docker Compose

---

## Prerequisites

Make sure you have installed:

- Docker
- Docker Compose
- Java 21+
- Maven 3.9+

---

## Setup Instructions

### 1. Clone the repository

```
git clone <repo-url> 
cd hr-microservices-assassement
```

---

### 2. Build Services

You must build each service before starting the infrastructure.

#### Employee Service

```
cd services/employee-service
mvn clean package -DskipTests
```

#### Leave Service

```
cd services/leave-service
mvn clean package -DskipTests
```

---

### 3. Build Infrastructure

Go to the infra folder and build Docker images:

```
cd ../../infra
docker compose build --no-cache
```

---

### 4. Start the System

Run all services using Docker Compose:

```
docker compose up -d
```

---

### 5. Verify Running Containers

```
docker ps
```

You should see:

- employee-service
- leave-service
- kafka
- database (postgres)
- other infra services

---

## Testing the APIs

Use the provided Postman collection:

```
collection/
```

Import it into Postman and test endpoints for:

- Employee Service APIs
- Leave Service APIs
- Event-driven communication via Kafka

### E2E Tests — employee-service

```
# Go to
    cd services/employee-service
# Build the application
    mvn clean package -DskipTests
# Build the Docker image required for E2E tests
    docker build -t employee-service:test .
# Run E2E tests
    mvn test -Pe2e
```
### Unit & Integration Tests — leave-service
```
# Go to
    cd services/leave-service
# Run Unit Tests
    mvn clean test -Dtest=LeaveUseCaseTest -e
# Run Integration Tests
    mvn test -Dtest=LeaveRepositoryTest
```
