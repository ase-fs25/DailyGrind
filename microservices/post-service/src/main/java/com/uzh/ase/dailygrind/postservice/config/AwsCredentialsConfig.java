package com.uzh.ase.dailygrind.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * Configuration class to provide AWS credentials for the application.
 * <p>
 * This configuration is loaded when the active Spring profile is not "test". It provides a
 * static AWS credentials provider using the provided AWS access key and secret key.
 */
@Configuration
public class AwsCredentialsConfig {

    @Value("${dg.us.aws.access-key}")
    private String amazonAWSAccessKey;

    @Value("${dg.us.aws.secret-key}")
    private String amazonAWSSecretKey;

    /**
     * Provides an {@link AwsCredentialsProvider} bean for AWS access using static credentials.
     * <p>
     * This bean is only loaded when the active Spring profile is <strong>not</strong> "test".
     * It uses the {@code amazonAWSAccessKey} and {@code amazonAWSSecretKey} properties
     * to create basic AWS credentials.
     *
     * @return a static AWS credentials provider
     */
    @Bean
    @Profile("!test")
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey)
        );
    }
}
