## Architecture

![Architecture](./../img/Architecture.jpeg)

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
-
### AWS Cognito
We use **AWS Cognito** to handle user authentication and authorization. It allows us to securely manage user sign-up, sign-in, and access control without having to build and maintain our own authentication system. Cognito supports features, that can be implemented in the future, like multi-factor authentication, federated identities (e.g., login with Google or Facebook), and user pool management, making it a powerful solution for user identity management.

### AWS DynamoDB
**AWS DynamoDB** is a fully managed NoSQL database that we use to store both user data, post content and push notification informaion. Its high performance at scale, low-latency access, and flexible schema design make it ideal for our application's evolving data requirements. With built-in support for key-value and document data models, DynamoDB ensures our application remains fast and responsive under heavy load.

### AWS SNS
We use **AWS Simple Notification Service (SNS)** to send real-time notifications to users and trigger workflows across services. SNS supports both push and fan-out messaging patterns, allowing us to notify users via multiple channels (e.g., email, SMS, Lambda) or inform multiple downstream systems simultaneously when important events occur, like a new post or a system alert.

### AWS SQS
**AWS Simple Queue Service (SQS)** helps us decouple microservices and process events asynchronously. By using message queues, we can buffer and retry workloads, improve system reliability, and scale individual components independently. This ensures that temporary failures or delays in one service don't impact the others, which is crucial for a robust microservices architecture.

### AWS Lambda
**AWS Lambda** enables us to run code in response to events without provisioning or managing servers. We use Lambda for lightweight, on-demand tasks such as sending user notifications or processing SQS messages. Its seamless integration with other AWS services like SNS, S3, and DynamoDB allows us to create powerful, event-driven workflows with minimal infrastructure overhead.

### AWS EventBridge
We use **AWS EventBridge** to schedule and route events within our system. For example, we can configure it to trigger daily notification jobs or monitor custom application events. EventBridge provides a highly scalable and reliable event bus that simplifies building event-driven applications, including recurring tasks and cross-service communication.

### AWS S3
**AWS Simple Storage Service (S3)** is our solution for storing static assets like user-uploaded images and videos. S3 offers virtually unlimited storage with high durability, versioning support, and fine-grained access control. It integrates well with services like CloudFront and Lambda, making it a powerful backend for media handling and content delivery.

### AWS ECS
**AWS Elastic Container Service (ECS)** is used to deploy and manage our containerized microservices. ECS provides deep integration with other AWS services and supports both EC2 and Fargate launch types. It allows us to automate service scaling, monitor performance, and easily roll out new versions, ensuring a robust and flexible deployment pipeline.

### AWS API Gateway
**AWS API Gateway** is responsible for exposing our backend services as secure and scalable RESTful APIs. It manages request routing, input validation, rate limiting, and authentication via Cognito or other identity providers. With built-in support for monitoring and throttling, API Gateway acts as a critical layer for securing and managing access to our microservices.

## Development Setup

Run the docker compose with the dev profile with the flag that it should rebuild the images. This is necessary to ensure that the terraform container is rebuilt to include the awslocal cli. This is used to automatically configure the localstack services.

Run the following command in the root directory to start the docker compose, make sure to have your LOCALSTACK_AUTH_TOKEN set as an env var to ensure that localstack pro is running correctly:
```bash
  docker compose -p dailygrind --profile dev up -d --build
```

For frontend development, the prfile `fe-dev` can be used
