package com.uzh.ase.dailygrind.postservice.post.sqs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class SqsConfig {

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    @Bean
    public SqsClient sqsClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SqsClient.builder()
            .endpointOverride(URI.create(awsBaseUrl))
            .credentialsProvider(awsCredentialsProvider)
            .region(Region.of(awsRegion))
            .build();
    }

}
