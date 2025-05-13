package com.uzh.ase.dailygrind.pushnotificationsservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

import java.net.URI;

/**
 * Configuration class responsible for setting up the AWS Lambda client.
 * <p>
 * This class configures the Lambda client to interact with AWS Lambda, including setting the endpoint URL and region.
 * It provides a bean for creating the Lambda client, which can be injected into other components that need to invoke AWS Lambda functions.
 */
@Configuration
@Profile("!test")
public class LambdaClientConfig {

    /**
     * The AWS base URL (for LocalStack or real AWS).
     * This value is injected from the application properties file.
     */
    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    /**
     * The AWS region used for AWS Lambda service.
     * This value is injected from the application properties file.
     */
    @Value("${dg.us.aws.region}")
    private String awsRegion;

    /**
     * Provides a Lambda client for interacting with AWS Lambda.
     * This bean is only active for non-test profiles.
     *
     * @return The configured Lambda client.
     */
    @Bean
    public LambdaClient lambdaClient() {
        return LambdaClient.builder()
            .endpointOverride(URI.create(awsBaseUrl))
            .region(Region.of(awsRegion))
            .build();
    }
}
