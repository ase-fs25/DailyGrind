package com.uzh.ase.dailygrind.userservice.config;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.FriendshipEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

/**
 * Configures DynamoDB client and tables for the application.
 * <p>
 * This configuration creates a {@link DynamoDbClient} and a {@link DynamoDbEnhancedClient} for accessing DynamoDB.
 * It also creates tables for User, Job, Education, and Friendship entities, using the appropriate schema mappings.
 * </p>
 */
@Configuration
public class DynamoDBConfig {

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    private static final String TABLE_NAME = "users";

    /**
     * Creates a DynamoDB client for interacting with DynamoDB.
     * <p>
     * This client uses the provided AWS credentials, region, and endpoint URL. It is used to interact with DynamoDB
     * and perform low-level operations.
     * </p>
     *
     * @param credentialsProvider the AWS credentials provider to authenticate requests
     * @return the DynamoDB client
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
     * Creates a DynamoDB Enhanced Client for working with enhanced DynamoDB functionality.
     * <p>
     * The enhanced client provides higher-level abstractions for working with DynamoDB tables.
     * </p>
     *
     * @param dynamoDbClient the low-level DynamoDB client
     * @return the enhanced DynamoDB client
     */
    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }

    /**
     * Creates a DynamoDB table for {@link UserEntity} with the defined schema.
     * <p>
     * This table allows CRUD operations on user data stored in DynamoDB.
     * </p>
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDB client
     * @return the DynamoDB table for users
     */
    @Bean
    public DynamoDbTable<UserEntity> userTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserEntity.class));
    }

    /**
     * Creates a DynamoDB table for {@link UserJobEntity} with the defined schema.
     * <p>
     * This table allows CRUD operations on job data associated with users.
     * </p>
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDB client
     * @return the DynamoDB table for user jobs
     */
    @Bean
    public DynamoDbTable<UserJobEntity> jobTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserJobEntity.class));
    }

    /**
     * Creates a DynamoDB table for {@link UserEducationEntity} with the defined schema.
     * <p>
     * This table allows CRUD operations on education data associated with users.
     * </p>
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDB client
     * @return the DynamoDB table for user education
     */
    @Bean
    public DynamoDbTable<UserEducationEntity> educationTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserEducationEntity.class));
    }

    /**
     * Creates a DynamoDB table for {@link FriendshipEntity} with the defined schema.
     * <p>
     * This table allows CRUD operations on friendship data associated with users.
     * </p>
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDB client
     * @return the DynamoDB table for friendships
     */
    @Bean
    public DynamoDbTable<FriendshipEntity> friendRequestTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FriendshipEntity.class));
    }
}
