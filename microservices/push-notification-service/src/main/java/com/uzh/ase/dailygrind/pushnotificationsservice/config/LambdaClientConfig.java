package com.uzh.ase.dailygrind.pushnotificationsservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

import java.net.URI;

@Configuration
public class LambdaClientConfig {

    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Bean
    public LambdaClient lambdaClient() {
        return LambdaClient.builder()
            .endpointOverride(URI.create(awsBaseUrl))
            .region(Region.of(awsRegion))
            .build();
    }
}
