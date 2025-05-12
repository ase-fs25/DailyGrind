# Push Notification Service

## Overview

This microservice is responsible for managing and sending web push notifications to subscribed users within the DailyGrind application. It leverages AWS serverless technologies to schedule and deliver browser notifications using the standard Web Push protocol.

---

## Features

- Manages user browser subscriptions
- Sends encrypted Web Push notifications
- Uses AWS Lambda for event-driven delivery
- Scheduled daily reminders via AWS EventBridge
- Built-in Spring Security with OAuth2/JWT authentication

---

## Technical Stack

- Java 17 + Spring Boot
- Spring Security (OAuth2 Resource Server with JWT)
- AWS Lambda (serverless notification dispatch)
- AWS EventBridge (notification scheduling)
- Amazon DynamoDB (NoSQL subscription storage)
- Web Push Protocol (with VAPID encryption)
- Docker & Docker Compose
- LocalStack for AWS service emulation
- Maven for build and dependency management

---


---

## API Endpoints

### Push Notifications

- `POST /push-notifications/subscribe`  
  Subscribes a user's browser client to notifications. Stores the payload in DynamoDB.

- `POST /push-notifications/send`  
  Sends a push notification to all subscribed users.

> All endpoints are secured via OAuth2 with JWT validation.

---

## Configuration

### Spring Security

- OAuth2 Resource Server enabled with JWT validation
- Protected endpoints (`/send`, `/subscribe`) require valid access tokens
- Configurable via `application.yaml`

### AWS Integration

- Uses AWS SDKs to interact with:
  - **DynamoDB** for storing subscriptions
  - **Lambda** for notification dispatch logic
  - **EventBridge** to trigger notifications on a daily schedule
- Terraform output (e.g., Cognito user pool ID, VAPID keys) should be injected into `application.yaml` post-deployment

---

## Local Development

> See [`../../SETUP.md`](../../SETUP.md) for complete setup and Docker instructions.

- Use **LocalStack** to emulate AWS services locally
- Use `application-dev.yaml` for local testing with mocked credentials and endpoints
- Run tests to validate configuration and endpoint behavior against LocalStack

---

## Testing Strategy

This service includes both **unit** and **integration** tests:

- **Unit tests** verify Web Push logic, encryption, and payload parsing
- **Integration tests**:
  - Simulate full end-to-end flow (subscribe → store in DynamoDB → send)
  - Run against **LocalStack** to validate AWS interactions
- **Test configs** (`AwsTestCredentialsConfig`, `LocalStackTestConfig`) isolate the AWS layer for predictable tests
- Tests run automatically as part of the GitHub Actions CI pipeline

---

## Technology Choices & Motivation

### AWS Lambda

- Stateless and scalable delivery mechanism
- Pay-per-execution: no idle server costs
- Seamlessly integrates with EventBridge and DynamoDB

### DynamoDB

- Flexible schema to store subscription payloads
- Ultra-fast read/write access
- Serverless scaling and cost-efficiency

### EventBridge

- Cron-based daily triggers for notification dispatch
- Decouples scheduling from core application logic
- Dynamically configurable and reliable

### Web Push Protocol

- Compatible with all modern browsers (Chrome, Firefox, Safari)
- Uses VAPID public/private key pairs for encrypted payload delivery
- Allows offline queuing and delivery upon user reconnect

---

## Deployment Notes

- All AWS resources are provisioned using Terraform
- Application-specific secrets (JWT secret, VAPID keys) are passed as environment variables or stored securely
- Microservice is packaged via Docker and deployable independently




