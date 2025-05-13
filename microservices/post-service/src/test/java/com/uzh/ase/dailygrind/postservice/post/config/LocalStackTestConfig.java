package com.uzh.ase.dailygrind.postservice.post.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;


import java.io.IOException;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@TestConfiguration(proxyBeanMethods = false)
public class LocalStackTestConfig {

    private static final String QUEUE_NAME = "user-events-consumer-queue";

    @Bean
    public LocalStackContainer localstackContainer() throws IOException, InterruptedException {
        LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(DYNAMODB, SQS);
        localstack.start();
        localstack.execInContainer(
            "awslocal",
            "sqs",
            "create-queue",
            "--queue-name",
            QUEUE_NAME
        );
        return localstack;
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(LocalStackContainer localStack) {
        AwsBasicCredentials creds = AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey());
        return StaticCredentialsProvider.create(creds);
    }

    @Bean
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider credentialsProvider, LocalStackContainer localstack) {
        return DynamoDbClient.builder()
            .credentialsProvider(credentialsProvider)
            .endpointOverride(localstack.getEndpointOverride(DYNAMODB))
            .region(Region.of(localstack.getRegion()))
            .build();
    }

    @EventListener
    @SuppressWarnings("unchecked")
    public void onApplicationReady(ApplicationReadyEvent applicationReadyEvent) {
        ApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        applicationContext.getBean("postTable", DynamoDbTable.class).createTable();
    }

    @Bean
    public SqsClient sqsClient(AwsCredentialsProvider credentialsProvider, LocalStackContainer localstack) {
        return SqsClient.builder()
            .endpointOverride(localstack.getEndpointOverride(SQS))
            .region(Region.of(localstack.getRegion()))
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry, LocalStackContainer localstack) {
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
        );

        SqsClient sqsClient = SqsClient.builder()
            .endpointOverride(localstack.getEndpointOverride(SQS))
            .region(Region.of(localstack.getRegion()))
            .credentialsProvider(credentialsProvider)
            .build();

        // Create queue early
        sqsClient.createQueue(CreateQueueRequest.builder().queueName(QUEUE_NAME).build());

        // Expose the URL so Spring config can inject it
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(QUEUE_NAME).build()).queueUrl();
        registry.add("sqs.queue-url", () -> queueUrl);
    }

}
