package com.uzh.ase.dailygrind.pushnotificationsservice.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.PushSubscriptionRepository;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service.PushNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
    "dg.lambda.function-name=test-lambda-function"
})
public class PushNotificationServiceIntegrationTest {

    @Autowired
    private PushNotificationService pushNotificationService;

    @MockBean
    private PushSubscriptionRepository pushSubscriptionRepository;

    @MockBean
    private LambdaClient lambdaClient;

    @Autowired
    private ObjectMapper objectMapper;

    private SubscriptionDto subscriptionDto;
    private PushSubscription pushSubscription;

    @BeforeEach
    void setup() {
        subscriptionDto = new SubscriptionDto(
            "https://example.com/endpoint",
            null,
            Map.of("p256dh", "key1", "auth", "key2")
        );

        pushSubscription = PushSubscription.builder()
            .subscriptionId("SUBSCRIPTION#1234")
            .userId("testUser")
            .endpoint(subscriptionDto.endpoint())
            .expirationTime(subscriptionDto.expirationTime())
            .keys(subscriptionDto.keys())
            .build();

        when(pushSubscriptionRepository.save(any(PushSubscription.class))).thenReturn(pushSubscription);
        when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(InvokeResponse.builder().build());    }

    @Test
    void shouldSaveSubscription() {
        // When
        PushSubscription result = pushNotificationService.saveSubscription(subscriptionDto, "testUser");

        // Then
        verify(pushSubscriptionRepository).save(any(PushSubscription.class));
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("testUser");
        assertThat(result.getEndpoint()).isEqualTo(subscriptionDto.endpoint());
    }

    @Test
    void shouldSendNotification() {
        // Given
        when(pushSubscriptionRepository.findAll()).thenReturn(Collections.singletonList(pushSubscription));

        // When
        pushNotificationService.sendNotification("Test message");

        // Then
        verify(lambdaClient).invoke(any(InvokeRequest.class));    }

    @Test
    void shouldThrowExceptionWhenNoSubscriptions() {
        // Given
        when(pushSubscriptionRepository.findAll()).thenReturn(Collections.emptyList());

        // When / Then
        Exception exception = assertThrows(RuntimeException.class, () ->
            pushNotificationService.sendNotification("Test message"));

        assertThat(exception.getMessage()).isEqualTo("No subscriptions found");
        verify(lambdaClient, never()).invoke(any(InvokeRequest.class));    }
}
