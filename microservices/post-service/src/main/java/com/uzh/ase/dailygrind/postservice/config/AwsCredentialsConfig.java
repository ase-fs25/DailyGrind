package com.uzh.ase.dailygrind.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * Configuration class for setting up AWS credentials.
 * <p>
 * This class reads the AWS access key and secret key from application properties and configures
 * the {@link AwsCredentialsProvider} to be used by the AWS SDK to authenticate requests.
 * The credentials are only provided if the profile is not 'test' to avoid exposing them during tests.
 */
@Configuration
public class AwsCredentialsConfig {

    @Value("${dg.us.aws.access-key}")
    private String amazonAWSAccessKey;

    @Value("${dg.us.aws.secret-key}")
    private String amazonAWSSecretKey;

    /**
     * Configures an {@link AwsCredentialsProvider} bean for AWS SDK authentication.
     * <p>
     * This method creates an {@link AwsCredentialsProvider} using the access and secret key provided
     * in the application's configuration. The credentials provider is only created when the application
     * is not running in the 'test' profile.
     *
     * @return an {@link AwsCredentialsProvider} configured with the access and secret key
     */
    @Bean
    @Profile("!test")
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey)
        );
    }
}
