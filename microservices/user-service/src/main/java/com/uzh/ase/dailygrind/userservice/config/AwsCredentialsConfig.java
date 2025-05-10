package com.uzh.ase.dailygrind.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration
public class AwsCredentialsConfig {

    @Value("${dg.us.aws.access-key}")
    private String amazonAWSAccessKey;

    @Value("${dg.us.aws.secret-key}")
    private String amazonAWSSecretKey;

    @Bean
    @Profile("!test")
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey)
        );
    }
}
