package com.uzh.ase.dailygrind.mspushnotifications.config;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.uzh.ase.dailygrind.mspushnotifications.pushNotification.repository.PushSubscriptionRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableDynamoDBRepositories(basePackageClasses = {PushSubscriptionRepository.class})
public class DynamoDBConfig {

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    @Value("${dg.us.aws.access-key}")
    private String amazonAWSAccessKey;

    @Value("${dg.us.aws.secret-key}")
    private String amazonAWSSecretKey;

    private AWSCredentialsProvider awsDynamoDBCredentials() {
        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey));
    }

    @Primary
    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig() {
        return DynamoDBMapperConfig.DEFAULT;
    }

    @Bean
    @Primary
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB,
                                         DynamoDBMapperConfig config) {
        return new DynamoDBMapper(amazonDynamoDB, config);
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {

        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsBaseUrl, awsRegion))
                .withCredentials(awsDynamoDBCredentials()).build();
    }
}