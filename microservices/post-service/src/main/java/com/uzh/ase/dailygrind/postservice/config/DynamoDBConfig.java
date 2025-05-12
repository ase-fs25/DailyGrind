package com.uzh.ase.dailygrind.postservice.config;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

/**
 * Configuration class to configure DynamoDB client and tables for the application.
 * <p>
 * This class sets up the {@link DynamoDbClient}, {@link DynamoDbEnhancedClient}, and
 * specific tables for various entities like {@link PostEntity}, {@link CommentEntity},
 * {@link LikeEntity}, etc. based on the provided AWS credentials and region.
 * </p>
 */
@Configuration
public class DynamoDBConfig {

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    private static final String TABLE_NAME = "posts";

    /**
     * Creates and configures a {@link DynamoDbClient} bean for use in the application.
     * <p>
     * This client is only loaded when the active profile is <strong>not</strong> "test".
     * It uses the provided {@link AwsCredentialsProvider}, a custom endpoint (e.g., LocalStack or AWS),
     * and the configured AWS region.
     * </p>
     *
     * @param credentialsProvider the AWS credentials provider to use for authentication
     * @return a configured {@link DynamoDbClient} instance
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
     * Creates a {@link DynamoDbEnhancedClient} using the provided low-level {@link DynamoDbClient}.
     *
     * @param dynamoDbClient the low-level DynamoDB client
     * @return a configured {@link DynamoDbEnhancedClient}
     */
    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }

    /**
     * Configures the DynamoDB table for {@link PostEntity}.
     *
     * @param dynamoDbEnhancedClient the enhanced client to use
     * @return a {@link DynamoDbTable} for {@link PostEntity}
     */
    @Bean
    public DynamoDbTable<PostEntity> postTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(PostEntity.class));
    }

    /**
     * Configures the DynamoDB table for {@link CommentEntity}.
     *
     * @param dynamoDbEnhancedClient the enhanced client to use
     * @return a {@link DynamoDbTable} for {@link CommentEntity}
     */
    @Bean
    public DynamoDbTable<CommentEntity> commentTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(CommentEntity.class));
    }

    /**
     * Configures the DynamoDB table for {@link LikeEntity}.
     *
     * @param dynamoDbEnhancedClient the enhanced client to use
     * @return a {@link DynamoDbTable} for {@link LikeEntity}
     */
    @Bean
    public DynamoDbTable<LikeEntity> likeTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(LikeEntity.class));
    }

    /**
     * Configures the DynamoDB table for {@link DailyPostEntity}.
     *
     * @param dynamoDbEnhancedClient the enhanced client to use
     * @return a {@link DynamoDbTable} for {@link DailyPostEntity}
     */
    @Bean
    public DynamoDbTable<DailyPostEntity> dailyPostTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(DailyPostEntity.class));
    }

    /**
     * Configures the DynamoDB table for {@link PinnedPostEntity}.
     *
     * @param dynamoDbEnhancedClient the enhanced client to use
     * @return a {@link DynamoDbTable} for {@link PinnedPostEntity}
     */
    @Bean
    public DynamoDbTable<PinnedPostEntity> pinnedPostTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(PinnedPostEntity.class));
    }

    /**
     * Configures the DynamoDB table for {@link UserEntity}.
     *
     * @param dynamoDbEnhancedClient the enhanced client to use
     * @return a {@link DynamoDbTable} for {@link UserEntity}
     */
    @Bean
    public DynamoDbTable<UserEntity> userTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserEntity.class));
    }

    /**
     * Configures the DynamoDB table for {@link FriendEntity}.
     *
     * @param dynamoDbEnhancedClient the enhanced client to use
     * @return a {@link DynamoDbTable} for {@link FriendEntity}
     */
    @Bean
    public DynamoDbTable<FriendEntity> friendTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FriendEntity.class));
    }

}
