# Post Service

## Overview

This microservice is responsible for managing user posts within the DailyGrind application. It allows users to create, read, update, and delete their posts, as well as manage comments and likes. The service is furthermore responsible for handling daily posts and ensuring that users are only able to post once per day. In, addition the service is responsible for managing the post feed, which is a collection of posts from users that the current user follows. Finally, this service also lets user pin certain posts to the top of their profile. This service also asynchronously consumes user and friendship events from a SQS. This service relies on this information to ensure that posts in the timeline and comments always have the most recent user data.

## Features

- User post management
- Comment management
- Like management
- Daily post management
- Timeline management
- Pinned post management
- Asynchronously connected to user service for user data via SNS nad SQS
- Time to live (TTL) for daily posts that automatically ensure that posts are deleted at 12:00 AM UTC

## Technical Stack
- Java Spring Boot
- Spring Security with OAuth2/JWT authentication
- AWS DynamoDB with dynamoDB enhanced client
- Single table design for post, comment, daily posts, pinned posts and like entities
- SQS Consumer for user data and friendship events

## Technology Choices & Motivation

### Java Spring Boot
We chose Java Spring Boot for the following reasons:
- Familiarity: The team has extensive experience with Java and Spring Boot, making it easier to develop and maintain the service.
- Microservices Architecture: Spring Boot is well-suited for building microservices, allowing us to create a lightweight and modular service.
- Integration: Spring Boot provides excellent integration with AWS services, making it easier to work with DynamoDB and SQS.
- Security: Spring Security provides robust authentication and authorization features, which are essential for protecting user data and ensuring secure access to the service.
- Community Support: Spring Boot has a large and active community, providing a wealth of resources and libraries to help with development.
- Testing: Spring Boot has built-in support for testing, making it easier to write and run unit and integration tests.
- Documentation: Spring Boot has extensive documentation, making it easier to understand and use the framework effectively.

### AWS DynamoDB
We chose AWS DynamoDB for the following reasons:
- Scalability: DynamoDB is a fully managed NoSQL database that can scale automatically to handle large amounts of data and traffic.
- Performance: DynamoDB provides single-digit millisecond response times, making it ideal for high-performance applications.
- Flexibility: DynamoDB's flexible schema allows us to store different types of data in a single table, making it easier to manage and query our data.
- Cost-Effectiveness: DynamoDB's pay-as-you-go pricing model allows us to only pay for the resources we use, making it a cost-effective solution for our needs.
- Integration: DynamoDB integrates seamlessly with other AWS services, such as SQS and Lambda, making it easier to build a complete serverless architecture.
- Single Table Design: DynamoDB's single table design allows us to store all of our data in a single table, making it easier to manage and query our data. A single table design is a common pattern in DynamoDB that allows us to store different types of entities (e.g., posts, comments, likes) in a single table. This approach simplifies data modeling and reduces the number of tables we need to manage. By using a single table design, we can also take advantage of DynamoDB's powerful query capabilities to efficiently retrieve related data. View this [AWS Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/data-modeling-schema-social-network.html) and this [Article](https://aws.amazon.com/blogs/database/single-table-vs-multi-table-design-in-amazon-dynamodb/) for more information on single table design.

### AWS SQS
We chose AWS SQS for the following reasons:
- Decoupling: SQS allows us to decouple our microservices, making it easier to manage and scale our architecture.
- Reliability: SQS provides a reliable message queue that ensures messages are delivered at least once, making it ideal for handling asynchronous events.
- Scalability: SQS can handle large volumes of messages, making it suitable for high-traffic applications.
- SQS FIFO: We use SQS FIFO queues to ensure that messages are processed in the order they are sent, which is important for maintaining the integrity of our data.
- SQS together with SNS: We use SQS together with SNS to ensure that our microservices are decoupled and can communicate asynchronously. This allows us to handle events from the user service and friendship service without tightly coupling our services together. If we would only use SNS, we would have to handle the events in the post service directly, which would make it harder to scale and maintain our architecture. By using SQS, we can process the events asynchronously and ensure that our microservices are decoupled. Furthermore, SQS allows us to later implement other microservices that can consume the same events without having to change the existing services. This allows us to easily extend our architecture in the future.

### API Endpoints

All endpoints can be viewed in the openApi documentation. When starting the service locally, the swagger-ui can be accessed at `http://localhost:8081/swagger-ui/index.html`.

### Profiles

By running the application with the `dev` profile, the application will provide extensive logging, will disable the security filter and will use a shorter TTL for the posts. This is useful for local development and testing. It's best to run the application using IntelliJ run configurations.

### Environment Variables
- `AWS_REGION`: The AWS region to use for Cognito, DynamoDB and SQS.
- `LOCALSTACK_PORT`: The port to use for LocalStack. Default is `4566`.
- `AWS_ACCESS_KEY_ID`: The AWS access key ID to use for LocalStack. Default is `test`.
- `AWS_SECRET_ACCESS_KEY`: The AWS secret access key to use for LocalStack. Default is `test`.
- `AWS_COGNITO_USER_POOL_ID`: The Cognito user pool ID to use for LocalStack. Check the terraform container to see the user pool id.
- `AWS_SQS_QUEUE_URL`: The SQS queue URL to use for LocalStack. Check the terraform container to see the queue url.

### Tests
This service is extensively tested. It includes unit tests for the generated mapstruct mappers. These mappers are part of the core logic since they handle the connection between business dtos and db entities. Furthermore, every endpoint and every szenario is covered by integration tests using a dynamodb that gets started in a localstack instance using testcontainers.

## Documentation

The microservice is documented using OpenAPI 3.0. The documentation is automatically generated and can be viewed in the Swagger UI `http://localhost:8081/swagger-ui/index.html`. Furthermore, the microservice is also documented using JavaDoc. The JavaDoc can be generated using Maven and can be found in the `docs` directory. Open the `index.html` file in the `docs` directory to view the documentation.

## Development Setup

### Prerequisites
- Java 17
- Maven
- Docker

### Run the Service locally in dev mode
1. Start the docker compose with the dev profile according to the [README](../../terraform/README.md)
2. Start the microservice with the dev profile



