package com.uzh.ase.dailygrind.pushnotificationsservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalStackTestConfig {

    @Bean
    public LocalStackContainer localStackContainer() {
        LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(
                LocalStackContainer.Service.DYNAMODB);
        localstack.start();
        return localstack;
    }

}
