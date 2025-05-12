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
 * Configuration class for setting up DynamoDB client and tables.
 * <p>
 * This configuration class sets up the DynamoDB client, enhanced DynamoDB client, and DynamoDB tables for various entities,
 * including Post, Comment, Like, DailyPost, PinnedPost, User, and Friend. The configuration is only applied when the profile
 * is not "test" (specified by the {@code !test} profile annotation).
 */
@Configuration
public class DynamoDBConfig {

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    private static final String TABLE_NAME = "posts";

    /**
     * Creates and configures a DynamoDbClient instance for interacting with DynamoDB.
     * <p>
     * This bean configures the DynamoDbClient to connect to the DynamoDB service, using the provided credentials provider
     * and region, as well as a custom base URL for local or test environments.
     *
     * @param credentialsProvider the AWS credentials provider
     * @return the configured {@link DynamoDbClient} instance
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
     * Creates a DynamoDbEnhancedClient instance for higher-level operations on DynamoDB.
     * <p>
     * The enhanced client provides a more user-friendly API for interacting with DynamoDB, making it easier to manage
     * and query DynamoDB tables and entities.
     *
     * @param dynamoDbClient the DynamoDbClient instance
     * @return the configured {@link DynamoDbEnhancedClient} instance
     */
    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }

    /**
     * Creates and configures a DynamoDbTable for the PostEntity.
     * <p>
     * This bean provides a table for interacting with the DynamoDB table that stores PostEntity objects. It uses the
     * {@link TableSchema} to map the {@link PostEntity} class to the corresponding DynamoDB table.
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDb client
     * @return the configured {@link DynamoDbTable} for PostEntity
     */
    @Bean
    public DynamoDbTable<PostEntity> postTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(PostEntity.class));
    }

    /**
     * Creates and configures a DynamoDbTable for the CommentEntity.
     * <p>
     * This bean provides a table for interacting with the DynamoDB table that stores CommentEntity objects. It uses the
     * {@link TableSchema} to map the {@link CommentEntity} class to the corresponding DynamoDB table.
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDb client
     * @return the configured {@link DynamoDbTable} for CommentEntity
     */
    @Bean
    public DynamoDbTable<CommentEntity> commentTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(CommentEntity.class));
    }

    /**
     * Creates and configures a DynamoDbTable for the LikeEntity.
     * <p>
     * This bean provides a table for interacting with the DynamoDB table that stores LikeEntity objects. It uses the
     * {@link TableSchema} to map the {@link LikeEntity} class to the corresponding DynamoDB table.
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDb client
     * @return the configured {@link DynamoDbTable} for LikeEntity
     */
    @Bean
    public DynamoDbTable<LikeEntity> likeTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(LikeEntity.class));
    }

    /**
     * Creates and configures a DynamoDbTable for the DailyPostEntity.
     * <p>
     * This bean provides a table for interacting with the DynamoDB table that stores DailyPostEntity objects. It uses the
     * {@link TableSchema} to map the {@link DailyPostEntity} class to the corresponding DynamoDB table.
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDb client
     * @return the configured {@link DynamoDbTable} for DailyPostEntity
     */
    @Bean
    public DynamoDbTable<DailyPostEntity> dailyPostTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(DailyPostEntity.class));
    }

    /**
     * Creates and configures a DynamoDbTable for the PinnedPostEntity.
     * <p>
     * This bean provides a table for interacting with the DynamoDB table that stores PinnedPostEntity objects. It uses the
     * {@link TableSchema} to map the {@link PinnedPostEntity} class to the corresponding DynamoDB table.
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDb client
     * @return the configured {@link DynamoDbTable} for PinnedPostEntity
     */
    @Bean
    public DynamoDbTable<PinnedPostEntity> pinnedPostTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(PinnedPostEntity.class));
    }

    /**
     * Creates and configures a DynamoDbTable for the UserEntity.
     * <p>
     * This bean provides a table for interacting with the DynamoDB table that stores UserEntity objects. It uses the
     * {@link TableSchema} to map the {@link UserEntity} class to the corresponding DynamoDB table.
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDb client
     * @return the configured {@link DynamoDbTable} for UserEntity
     */
    @Bean
    public DynamoDbTable<UserEntity> userTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserEntity.class));
    }

    /**
     * Creates and configures a DynamoDbTable for the FriendEntity.
     * <p>
     * This bean provides a table for interacting with the DynamoDB table that stores FriendEntity objects. It uses the
     * {@link TableSchema} to map the {@link FriendEntity} class to the corresponding DynamoDB table.
     *
     * @param dynamoDbEnhancedClient the enhanced DynamoDb client
     * @return the configured {@link DynamoDbTable} for FriendEntity
     */
    @Bean
    public DynamoDbTable<FriendEntity> friendTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FriendEntity.class));
    }
}
