package com.uzh.ase.dailygrind.pushnotificationsservice.integrationTests;

import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.PushSubscriptionRepository;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@SpringBootTest
@Testcontainers
public class PushSubscriptionRepositoryIntegrationTest {

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
        .withServices(DYNAMODB);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint", () -> localStack.getEndpointOverride(DYNAMODB).toString());
        registry.add("aws.region", () -> localStack.getRegion());
        registry.add("aws.accessKeyId", () -> localStack.getAccessKey());
        registry.add("aws.secretKey", () -> localStack.getSecretKey());
    }

    @Autowired
    private PushSubscriptionRepository pushSubscriptionRepository;

    // Keep track of created subscription IDs for cleanup
    private final List<String> createdSubscriptionIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        // Delete all subscriptions created during the test
        for (String id : createdSubscriptionIds) {
            pushSubscriptionRepository.deleteById(id);
        }
        createdSubscriptionIds.clear();
    }

    @Test
    void shouldSaveAndRetrievePushSubscription() {
        // Given
        PushSubscription subscription = PushSubscription.builder()
            .userId("testUser")
            .endpoint("https://example.com/endpoint")
            .expirationTime(null)
            .keys(Map.of("p256dh", "key1", "auth", "key2"))
            .build();
        subscription.generateId();
        String subscriptionId = subscription.getSubscriptionId();

        // Add to cleanup list
        createdSubscriptionIds.add(subscriptionId);

        // When
        pushSubscriptionRepository.save(subscription);
        List<PushSubscription> allSubscriptions = pushSubscriptionRepository.findAll();

        // Then
        assertThat(subscriptionId).isNotNull();

        // Find the saved subscription in the list by ID
        Optional<PushSubscription> retrievedOpt = allSubscriptions.stream()
            .filter(s -> subscriptionId.equals(s.getSubscriptionId()))
            .findFirst();

        assertThat(retrievedOpt).isPresent();
        PushSubscription retrieved = retrievedOpt.get();

        // Compare individual fields
        assertThat(retrieved.getSubscriptionId()).isEqualTo(subscriptionId);
        assertThat(retrieved.getUserId()).isEqualTo("testUser");
        assertThat(retrieved.getEndpoint()).isEqualTo("https://example.com/endpoint");
        assertThat(retrieved.getKeys()).isEqualTo(Map.of("p256dh", "key1", "auth", "key2"));
    }

    @Test
    void shouldFindById() {
        // Given
        PushSubscription subscription = PushSubscription.builder()
            .userId("testUser")
            .endpoint("https://example.com/endpoint-findbyid")
            .expirationTime(null)
            .keys(Map.of("p256dh", "key1", "auth", "key2"))
            .build();
        subscription.generateId();
        String subscriptionId = subscription.getSubscriptionId();

        // Add to cleanup list
        createdSubscriptionIds.add(subscriptionId);

        // When
        pushSubscriptionRepository.save(subscription);
        Optional<PushSubscription> result = pushSubscriptionRepository.findById(subscriptionId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSubscriptionId()).isEqualTo(subscriptionId);
        assertThat(result.get().getUserId()).isEqualTo("testUser");
    }

    @Test
    void shouldDeleteById() {
        // Given
        PushSubscription subscription = PushSubscription.builder()
            .userId("testUser")
            .endpoint("https://example.com/endpoint-delete")
            .expirationTime(null)
            .keys(Map.of("p256dh", "key1", "auth", "key2"))
            .build();
        subscription.generateId();
        String subscriptionId = subscription.getSubscriptionId();
        pushSubscriptionRepository.save(subscription);

        // When
        pushSubscriptionRepository.deleteById(subscriptionId);

        // Then
        Optional<PushSubscription> result = pushSubscriptionRepository.findById(subscriptionId);
        assertThat(result).isEmpty();
    }
}
