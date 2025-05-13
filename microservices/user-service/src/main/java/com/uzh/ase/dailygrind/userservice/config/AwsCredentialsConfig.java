package com.uzh.ase.dailygrind.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * Configuration class for providing AWS credentials to the application.
 * This configuration will create an AWS credentials provider that is used for interacting with AWS services.
 */
@Configuration
public class AwsCredentialsConfig {

    /**
     * AWS Access Key for accessing AWS services.
     * The value is injected from the application properties file.
     */
    @Value("${dg.us.aws.access-key}")
    private String amazonAWSAccessKey;

    /**
     * AWS Secret Key for accessing AWS services.
     * The value is injected from the application properties file.
     */
    @Value("${dg.us.aws.secret-key}")
    private String amazonAWSSecretKey;

    /**
     * Creates an {@link AwsCredentialsProvider} bean that uses static AWS credentials.
     * This bean is only created for profiles other than "test" (i.e., not in the test environment).
     *
     * @return an {@link AwsCredentialsProvider} instance using the provided AWS credentials.
     */
    @Bean
    @Profile("!test") // This bean is not loaded for the "test" profile
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey)
        );
    }
}
