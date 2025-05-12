# Push Notification Service

## Overview
This microservice is responsible for managing and sending web push notifications to subscribed users within the DailyGrind application.

## Features
- Web push notification management
- User push notification subscription handling
- Integration with AWS Lambda for notification delivery
- Daily scheduled notifications via EventBridge

## Technical Stack
- Java Spring Boot
- Spring Security with OAuth2/JWT authentication
- AWS Lambda integration for notification delivery
- Web Push protocol implementation

## Technology Choices & Motivation

### AWS Lambda
We chose AWS Lambda for notification delivery due to its:
- Serverless architecture removing the need for managing notification servers
- Event-driven nature perfectly suiting the asynchronous notification workflow
- Cost-effectiveness - only paying for actual notification sending time
- High scalability to handle notification bursts during daily notification

### DynamoDB
DynamoDB was selected for subscription storage because:
- Its NoSQL structure accommodates the varying subscription payload formats
- Provides single-digit millisecond response times for quick notification dispatch
- Automatic scaling handles growing user subscription base without performance degradation

### EventBridge
EventBridge powers our notification scheduling because:
- It provides precise cron-based time scheduling for consistent daily reminders
- Decouples the scheduling mechanism from the notification logic
- Enables easy adjustment of notification timing based on user feedback
- Highly reliable event delivery ensures notifications are triggered consistently

### Web Push Protocol
We implemented the Web Push standard because:
- It works across modern browsers without proprietary implementations
- Supports offline notification delivery when users return online
- Provides end-to-end encryption for user privacy
- Doesn't require keeping persistent connections open to each client

## API Endpoints

### Push Notifications
- `POST /push-notifications/send` - Send push notifications to subscribers
- `POST /push-notifications/subscribe` - Subscribe a user to push notifications

## Documentation

The microservice is also documented using JavaDoc. The JavaDoc can be generated using Maven and can be found in the `docs` directory. Open the `index.html` file in the `docs` directory to view the documentation.

## Configuration

### Security
The service is configured with Spring Security:
- JWT-based authentication for protected endpoints

## Development Setup

### Prerequisites
- Java 17 or higher
- Maven 3.x
- Docker and Docker Compose

### Running Locally
1. Build the service
2. Start the service with dependencies using Docker Compose
3. From Terraform folder output get the daily_grind_user_pool_id and set it in the application.yaml file


