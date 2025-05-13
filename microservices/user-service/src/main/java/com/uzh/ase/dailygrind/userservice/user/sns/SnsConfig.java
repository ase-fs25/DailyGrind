package com.uzh.ase.dailygrind.userservice.user.sns;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import java.net.URI;

/**
 * Configuration class for setting up the SNS (Simple Notification Service) client.
 * <p>
 * This class configures the SNS client to interact with AWS SNS services, either in the AWS cloud or a LocalStack instance,
 * depending on the environment configuration.
 */
@Configuration
public class SnsConfig {

    /**
     * The AWS region where the SNS client will connect.
     * This value is injected from the application properties file.
     */
    @Value("${dg.us.aws.region}")
    private String awsRegion;

    /**
     * The base URL for the SNS service.
     * This value is injected from the application properties file.
     */
    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    /**
     * Configures and returns an {@link SnsClient} to interact with the SNS service.
     * <p>
     * The SNS client is configured with a custom endpoint (for LocalStack or AWS), credentials provider, and region.
     *
     * @param awsCredentialsProvider The credentials provider used for AWS authentication.
     * @return The configured {@link SnsClient}.
     */
    @Bean
    public SnsClient snsClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SnsClient.builder()
            .endpointOverride(URI.create(awsBaseUrl))
            .credentialsProvider(awsCredentialsProvider)
            .region(Region.of(awsRegion))
            .build();
    }
}
