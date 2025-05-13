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
- AWS DynamoDB with dynamoDB enhanced client
- AWS Lambda integration for notification delivery
- AWS EventBridge Scheduler for managing the timely based push notifications
- Web Push protocol implementation adhering to the PushAPI standard

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

### Profiles

By running the application with the `dev` profile, the application will provide extensive logging, will disable the security filter. This is useful for local development and testing. It's best to run the application using IntelliJ run configurations.

## Documentation

The microservice is documented using OpenAPI 3.0. The documentation is automatically generated and can be viewed in the Swagger UI `http://localhost:8081/swagger-ui/index.html`. Furthermore, the microservice is also documented using JavaDoc. The JavaDoc can be generated using Maven and can be found in the `docs` directory. Open the `index.html` file in the `docs` directory to view the documentation.

### Environment Variables
- `AWS_REGION`: The AWS region to use for Cognito, DynamoDB and SQS.
- `LOCALSTACK_PORT`: The port to use for LocalStack. Default is `4566`.
- `AWS_ACCESS_KEY_ID`: The AWS access key ID to use for LocalStack. Default is `test`.
- `AWS_SECRET_ACCESS_KEY`: The AWS secret access key to use for LocalStack. Default is `test`.
- `AWS_COGNITO_USER_POOL_ID`: The Cognito user pool ID to use for LocalStack. Check the terraform container to see the user pool id.
- `LAMBDA_FUNCTION_NAME`: Name of the lambda function that handles the web push notifications.

### Tests
This service is extensively tested. It includes unit tests for the controller and service layers as well as for the lambda functions. Furthermore, endpoints and different scenarios are covered by integration tests using a dynamodb that gets started in a localstack instance using testcontainers.

## Development Setup

### Prerequisites
- Java 17
- Maven
- Docker and Docker Compose

### Run the Service locally in dev mode
1. Start the docker compose with the dev profile according to the [README](../../README.md)
2. Start the microservice with the dev profile


