# DailyGrind

**DailyGrind** is a full-stack microservices-based social productivity platform that combines authentic daily check-ins with professional networking features. Think LinkedIn meets BeReal, designed and implemented with industry-grade tools and scalable cloud architecture.

Built during the *Advanced Software Engineering* course at the University of Zurich, the platform showcases a modern DevOps-ready monorepo using Spring Boot, React, AWS, Terraform, and GitHub Actions.

---


### Microservices

Each backend microservice is organized under `microservices/` and includes a dedicated `README.md` with its own configuration, endpoints, and AWS integrations:

- [user-service](microservices/user-service/README.md) — Handles user profiles, education, jobs, friends, and SNS event publishing  
- [post-service](microservices/post-service/README.md) — Manages user-generated posts  
- [push-notification-service](microservices/push-notification-service/README.md) — Handles scheduled push notifications using AWS EventBridge and Lambda  

Each service is self-contained, follows a consistent domain-driven structure, and is containerized for standalone deployment.

---

## Technical Highlights

### Architecture

DailyGrind is structured as a **modular monorepo**, following a domain-driven, microservices-first approach. Each service is built in Java using Spring Boot and adheres to a layered architecture:

- Backend is divided into **independently deployable Spring Boot services**
- REST-based communication between services, secured by **OAuth2 JWT authentication**
- Each service contains:
  - `controller/`: REST endpoints
  - `service/`: Business logic layer
  - `repository/`: JPA repositories
  - `entity/`: Domain models
  - `mapper/`: DTO mapping layer
  - `config/`: Spring and AWS-related configuration
  - `sns/` (if applicable): For event publishing via AWS SNS
- Environment-specific configs are handled via `application.yaml` profiles
- Frontend and backend are fully **Dockerized**
- **Terraform** provisions AWS infrastructure
- **LocalStack** is used for local AWS service emulation (Lambda, EventBridge, DynamoDB)

### Testing Strategy

Each microservice includes a comprehensive test suite organized under `src/test/`, with the following testing layers:

- **Unit Tests**: For mapping logic and service-level behavior
- **Integration Tests**:
  - Cover controller + service interactions
  - Validate behavior against LocalStack-mocked AWS services (DynamoDB, SNS, etc.)
- **Test Configuration**:
  - `AwsTestCredentialsConfig`, `LocalStackTestConfig`, etc., for isolated test environments
  - Dynamically wired profiles (`application-dev.yaml`) ensure testability without real AWS dependencies
- Test execution is integrated into the **GitHub Actions** CI pipeline, ensuring continuous validation on every push or pull request

### Frontend

- React (Vite) with TypeScript for fast development and strict typing
- Global state is managed with React Context and hooks
- Modular design: components like `FriendPopup`, `AddPostPopup`, `EducationSection`, `JobsSection`
- Progressive Web App (PWA) integration with custom `service-worker.js`
- Web Push notifications integrated directly with user actions and AWS backend triggers

### Authentication & Security

- OAuth2 Resource Server (Spring Security)
- JWT tokens issued by the `auth-service` and verified across services
- Role-based access control (future-proofed for admin/user roles)
- All REST endpoints secured by token-based authorization

---

## AWS + Terraform + Serverless

### Provisioning via Terraform

The `terraform/` module provisions all necessary AWS components:

- Cognito User Pool: Centralized auth management
- EventBridge: Triggers scheduled daily push events
- Lambda Functions: Serverless push notification dispatch
- DynamoDB: NoSQL store for user subscription payloads

Example flow:

EventBridge (daily 19:00) → Lambda → Query DynamoDB → Send Web Push


All of this can be tested locally via LocalStack without requiring AWS credentials.

---

## Push Notification Infrastructure

Full details in `push-notification-service/README.md`.

- Standard Web Push Protocol (with VAPID encryption)
- Subscriptions stored in DynamoDB
- Push logic dispatched via AWS Lambda
- Scheduler powered by EventBridge cron rules
- Offline delivery supported by PWA-ready service worker
- Integrated with Spring Security for access control

---

## DevOps & CI/CD

### GitHub Actions Workflows

- Per-service workflows (e.g. `microservices.yml`, `frontend.yml`)
- Triggered on every pull request or push to main/develop
- Steps: Lint → Test → SonarQube Analysis → Docker Build

### Code Quality with SonarQube

- Applied to all Java microservices
- Enforces rules on:
  - Code duplication
  - Branch coverage
  - Complexity metrics
  - Security vulnerabilities
- Quality gates must pass before merge

### GitHub Merge Requests

- All development is done via feature branches
- Reviewed via pull requests with branch protection
- Reviewed for clean commit history, passing CI, and coverage

---

## Tech Stack Overview

| Layer              | Technologies & Tools                                                      |
|--------------------|---------------------------------------------------------------------------|
| Frontend           | React, Vite, TypeScript, Material UI                                      |
| Backend            | Spring Boot 3, Spring Security, Java 17                                   |
| Authentication     | OAuth2 Resource Server, JWT, AWS Cognito                                  |
| Persistence        | PostgreSQL (Docker), DynamoDB (LocalStack/AWS)                            |
| Notifications      | AWS Lambda, EventBridge, Web Push API                                     |
| Infrastructure     | Terraform (IaC), LocalStack                                                |
| CI/CD              | GitHub Actions, CodeQL, SonarQube                                         |
| Containerization   | Docker, Docker Compose                                                    |
| Monitoring (future)| CloudWatch-compatible logs, Sonar dashboards                              |

---

## Team

This project was developed by a cross-functional team :

- **Jonas Gebel** – Lead Frontend Developer  
- **Leonard Wagner** – Lead Backend Developer  
- **Mete Polat** – DevOps Engineer  
- **Tim Vorburger** – Full-Stack Developer  
- **Toni Krstic** – Full-Stack Developer  

---

## License

Distributed under the MIT License. See LICENSE for details.



