package com.uzh.ase.dailygrind.userservice.user.sns;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import java.net.URI;

@Configuration
public class SnsConfig {

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    @Bean
    public SnsClient snsClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SnsClient.builder()
            .endpointOverride(URI.create(awsBaseUrl))
            .credentialsProvider(awsCredentialsProvider)
            .region(Region.of(awsRegion))
            .build();
    }

}
