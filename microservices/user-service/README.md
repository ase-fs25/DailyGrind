# User Service

## Overview

The User Service handles all user-related data and interactions within the DailyGrind platform. It provides APIs for managing user profiles, education and job histories, and friend relationships. It also supports SNS event publishing for inter-service communication.

---

## Features

- CRUD operations for user profiles, education, and jobs
- Friend system (send, accept, reject, remove friends)
- Event publishing to AWS SNS for downstream service consumption
- JWT-secured REST API with Spring Security
- Integration with DynamoDB for optional AWS-driven features

---

## Technical Stack

- Java 17 + Spring Boot 3
- Spring Security (OAuth2 + JWT)
- PostgreSQL (via JPA + Spring Data)
- AWS SNS for event-based communication
- Docker & Docker Compose
- Maven for build and dependency management
- LocalStack for testing SNS and DynamoDB
- JUnit + Mockito + LocalStack for full test coverage

---

## API Endpoints

### User Management

- `GET /users/{id}` – Get user by ID  
- `POST /users` – Create new user  
- `PUT /users/{id}` – Update user  
- `DELETE /users/{id}` – Delete user  

### Job & Education

- `GET /users/jobs/{userId}` – Get jobs for a user  
- `POST /users/jobs` – Add job  
- `DELETE /users/jobs/{id}` – Delete job  

- `GET /users/education/{userId}` – Get education history  
- `POST /users/education` – Add education  
- `DELETE /users/education/{id}` – Delete education  

### Friend System

- `POST /users/friends/add` – Send friend request  
- `POST /users/friends/accept` – Accept friend request  
- `POST /users/friends/reject` – Reject friend request  
- `POST /users/friends/remove` – Remove friend  
- `GET /users/friends/{userId}` – Get all friends of a user  
- `GET /users/friends/pending/{userId}` – Get pending friend requests

---

## Configuration

### Security

- Uses OAuth2 Resource Server with JWT tokens
- All endpoints are protected except for public fetch routes
- JWT secrets are injected via `application.yaml` or environment variables

### SNS Integration

- Events (e.g., user updates, friend events) are published to SNS
- Topic ARNs are configured in `application.yaml`
- Uses `UserEventPublisher` to wrap and send events

---

## Local Development

> Full setup instructions available in [`../../SETUP.md`](../../SETUP.md)

- Use Docker Compose to run PostgreSQL + LocalStack
- LocalStack emulates SNS and (if needed) DynamoDB
- Profile switching via Spring Boot: `application-dev.yaml` vs `application.yaml`

---

## Testing Strategy

The service includes both **unit** and **integration** test layers:

- **Unit Tests**:
  - Mapper tests (entity ↔ DTO)
  - Isolated service logic

- **Integration Tests**:
  - Full controller → service → repository flows
  - Tested against PostgreSQL and mocked AWS services
  - Uses `LocalStackTestConfig` and test containers

- Tests are run automatically as part of the GitHub Actions CI pipeline.

---

## Deployment Notes

- Deployed as a Docker container
- Uses environment-based configuration (`dev`, `prod`)
- Externalized secrets (JWT, DB credentials, SNS ARNs) must be injected securely


