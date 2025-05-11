package com.uzh.ase.dailygrind.pushnotificationsservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.lambda.LambdaClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class LambdaTestConfig {
    @Bean
    public LambdaClient lambdaClient() {
        return mock(LambdaClient.class);
    }
}
