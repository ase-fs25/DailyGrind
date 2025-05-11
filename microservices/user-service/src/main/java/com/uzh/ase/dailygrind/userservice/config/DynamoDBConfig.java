package com.uzh.ase.dailygrind.userservice.config;


import com.uzh.ase.dailygrind.userservice.user.repository.entity.FriendRequestEntity;
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

@Configuration
public class DynamoDBConfig {

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    private static final String TABLE_NAME = "users";

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
    public DynamoDbTable<UserEntity> userTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserEntity.class));
    }

    @Bean
    public DynamoDbTable<UserJobEntity> jobTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserJobEntity.class));
    }

    @Bean
    public DynamoDbTable<UserEducationEntity> educationTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserEducationEntity.class));
    }

    @Bean
    public DynamoDbTable<FriendRequestEntity> friendRequestTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FriendRequestEntity.class));
    }

}
