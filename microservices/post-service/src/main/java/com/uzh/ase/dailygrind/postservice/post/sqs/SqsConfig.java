package com.uzh.ase.dailygrind.postservice.post.sqs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

/**
 * Configuration class for setting up the Amazon Simple Queue Service (SQS) client.
 * It uses AWS credentials and region configuration from application properties
 * and creates an instance of the SQS client for interacting with SQS services.
 */
@Profile("!test")
@Configuration
public class SqsConfig {

    /**
     * AWS region where the SQS service is hosted.
     * This value is injected from the application properties.
     */
    @Value("${dg.us.aws.region}")
    private String awsRegion;

    /**
     * The base URL for AWS services, typically used for LocalStack or custom endpoint URLs.
     * This value is injected from the application properties.
     */
    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    /**
     * Creates an instance of the SQS client with configured credentials provider, region, and endpoint.
     *
     * @param awsCredentialsProvider The AWS credentials provider to be used by the SQS client.
     * @return The configured SQS client.
     */
    @Bean
    public SqsClient sqsClient(AwsCredentialsProvider awsCredentialsProvider) {
        // Set the custom AWS service endpoint URL
        // Provide AWS credentials
        // Specify the AWS region
        return SqsClient.builder()
            .endpointOverride(URI.create(awsBaseUrl))  // Set the custom AWS service endpoint URL
            .credentialsProvider(awsCredentialsProvider)  // Provide AWS credentials
            .region(Region.of(awsRegion))  // Specify the AWS region
            .build();
    }

}
