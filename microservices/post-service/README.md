# Post Service

## Overview

The Post Service manages all user-generated posts within the DailyGrind platform. It handles post creation, editing, deletion, and retrieval, while enforcing authentication and data validation through Spring Security and structured DTOs.

---

## Features

- CRUD operations for user posts
- Each post can contain text, optional image URLs, and timestamps
- Posts are associated with user IDs from the authenticated JWT token
- JWT-secured REST API
- Layered Spring Boot architecture with full test coverage
- Dockerized for standalone deployment

---

## Technical Stack

- Java 17 + Spring Boot 3
- Spring Security (OAuth2 + JWT)
- PostgreSQL (via JPA)
- Docker & Docker Compose
- Maven (build, dependencies)
- JUnit, Mockito for testing
- GitHub Actions + SonarQube for CI and code quality

---

## API Endpoints

All endpoints are secured via JWT and require a valid access token.

### Post Management

- `POST /posts`  
  Create a new post (includes text, optional image)

- `GET /posts/user/{userId}`  
  Retrieve all posts by a specific user

- `GET /posts/friends/{userId}`  
  Retrieve posts of a user’s friends (based on friend list from `user-service`)

- `PUT /posts/{id}`  
  Edit an existing post

- `DELETE /posts/{id}`  
  Delete a post by ID

---

## Configuration

### Security

- Secured with OAuth2 Resource Server and JWT
- JWT token must be present in the `Authorization` header for all protected endpoints
- Authenticated user ID is extracted and used to associate posts

### Profiles

- `application.yaml` for default runtime
- `application-dev.yaml` used when running locally (e.g., via Docker Compose)

---

## Local Development

> Refer to the root-level [SETUP.md](../../SETUP.md) for environment instructions

- Start with Docker Compose to run PostgreSQL
- Use `application-dev.yaml` for local testing
- Frontend will send requests to this service after user authentication

---

## Testing Strategy

Includes both unit and integration tests:

- **Unit Tests**:
  - Mapper logic (Post ↔ DTO)
  - Service logic in isolation

- **Integration Tests**:
  - Full flow validation via controller
  - Uses an in-memory or containerized PostgreSQL instance
  - JWT decoding tested via mock tokens

- Tests run automatically in CI via GitHub Actions and are validated through SonarQube

---

## Deployment Notes

- The service is packaged as a Docker container
- Secrets (e.g., JWT secret, DB credentials) are injected via environment variables
- Can be deployed independently of other microservices

---



