## Architecture

### Microservices

The microservice is structured in a way that allows for easy development and deployment. The main components are:
- Post Service
- User Service
- Push Notification Service

User Service and Post Service are decoupled and communicate via AWS SNS and SQS. The Post Service consumes events from the User Service to ensure that posts in the timeline and comments always have the most recent user data. The Push Notification Service is responsible for sending web push notifications to users. By separating these services, we can ensure that each service is responsible for a specific task and can be developed and deployed independently. This allows for easier maintenance and scaling of the application.

### AWS Services
- AWS Cognito: Used for user authentication and authorization.
- AWS DynamoDB: Used for storing user- and post-data.
- AWS SNS: Used for sending notifications to users.
- AWS SQS: Used for decoupling the microservices and ensuring that events are processed asynchronously.
- AWS Lambda: Used for executing code in response to events, such as sending notifications.
- AWS EventBridge: Used for scheduling events, such as sending daily notifications.
- AWS S3: Used for storing static assets, such as images and videos.
- AWS ECS: Used for deploying and managing the microservices in a containerized environment.
- AWS API Gateway: Used for exposing the microservices as RESTful APIs.

## Development Setup

Run the docker compose with the dev profile with the flag that it should rebuild the images. This is necessary to ensure that the terraform container is rebuilt to include the awslocal cli. This is used to automatically configure the localstack services.

Run the following command in the root directory to start the docker compose, make sure to have your LOCALSTACK_AUTH_TOKEN set as an env var to ensure that localstack pro is running correctly:
```bash
  docker compose -p dailygrind --profile dev up -d --build
```

For frontend development, the prfile `fe-dev` can be used
