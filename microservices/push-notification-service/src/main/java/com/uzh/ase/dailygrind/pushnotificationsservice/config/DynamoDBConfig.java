package com.uzh.ase.dailygrind.pushnotificationsservice.config;

import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

/**
 * Configuration class responsible for setting up DynamoDB client and related components.
 * <p>
 * This class configures the DynamoDB client, enhanced client, and the specific table for push subscriptions.
 * It provides the necessary beans for interacting with DynamoDB, including credentials and region settings.
 */
@Configuration
public class DynamoDBConfig {

    /**
     * AWS region used for DynamoDB service.
     * This value is injected from the application properties file.
     */
    @Value("${dg.us.aws.region}")
    private String awsRegion;

    /**
     * AWS base URL (for LocalStack or real AWS).
     * This value is injected from the application properties file.
     */
    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    /**
     * AWS access key used for authentication.
     * This value is injected from the application properties file.
     */
    @Value("${dg.us.aws.access-key}")
    private String amazonAWSAccessKey;

    /**
     * AWS secret key used for authentication.
     * This value is injected from the application properties file.
     */
    @Value("${dg.us.aws.secret-key}")
    private String amazonAWSSecretKey;

    /**
     * The name of the DynamoDB table used for push subscriptions.
     */
    private static final String TABLE_NAME = "push-subscriptions";

    /**
     * Provides AWS credentials to be used for DynamoDB interactions.
     * This bean is only active for non-test profiles.
     *
     * @return AWS credentials provider using static credentials.
     */
    @Bean
    @Profile("!test")
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey)
        );
    }

    /**
     * Provides a DynamoDB client that interacts with DynamoDB service.
     * This bean is only active for non-test profiles.
     *
     * @param credentialsProvider The credentials provider for AWS authentication.
     * @return The configured DynamoDB client.
     */
    @Bean
    @Profile("!test")
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider credentialsProvider) {
        return DynamoDbClient.builder()
            .endpointOverride(URI.create(awsBaseUrl))
            .credentialsProvider(credentialsProvider)
            .region(Region.of(awsRegion))
            .build();
    }

    /**
     * Provides an enhanced DynamoDB client that simplifies DynamoDB operations.
     * This bean is only active for non-test profiles.
     *
     * @param dynamoDbClient The base DynamoDB client used to configure the enhanced client.
     * @return The configured enhanced DynamoDB client.
     */
    @Bean
    @Profile("!test")
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }

    /**
     * Provides the DynamoDB table for push subscriptions.
     * This bean interacts with the DynamoDB table named "push-subscriptions" and is used to read and write push subscription data.
     *
     * @param dynamoDbEnhancedClient The enhanced DynamoDB client.
     * @return The DynamoDB table for push subscriptions.
     */
    @Bean
    public DynamoDbTable<PushSubscription> pushSubscriptionTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(PushSubscription.class));
    }

}
