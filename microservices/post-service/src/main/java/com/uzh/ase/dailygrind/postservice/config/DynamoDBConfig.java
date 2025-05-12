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

@Configuration
public class DynamoDBConfig {

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    private static final String TABLE_NAME = "posts";

    @Bean
    @Profile("!test")
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider credentialsProvider) {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(awsBaseUrl))
                .credentialsProvider(credentialsProvider)
                .region(Region.of(awsRegion))
                .build();
    }

    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<PostEntity> postTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(PostEntity.class));
    }

    @Bean
    public DynamoDbTable<CommentEntity> commentTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(CommentEntity.class));
    }

    @Bean
    public DynamoDbTable<LikeEntity> likeTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(LikeEntity.class));
    }

    @Bean
    public DynamoDbTable<DailyPostEntity> dailyPostTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(DailyPostEntity.class));
    }

    @Bean
    public DynamoDbTable<PinnedPostEntity> pinnedPostTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(PinnedPostEntity.class));
    }

    @Bean
    public DynamoDbTable<UserEntity> userTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserEntity.class));
    }

    @Bean
    public DynamoDbTable<FriendEntity> friendTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FriendEntity.class));
    }

}
