package com.uzh.ase.dailygrind.pushnotificationsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.PushSubscriptionRepository;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service.PushNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PushNotificationServiceTest {
    @Mock
    private PushSubscriptionRepository pushSubscriptionRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PushNotificationService pushNotificationService;

    @Captor
    private ArgumentCaptor<PushSubscription> subscriptionArgumentCaptor;

    @Captor
    private ArgumentCaptor<InvokeRequest> invokeRequestArgumentCaptor;

    private SubscriptionDto subscriptionDto;
    private PushSubscription pushSubscription;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(pushNotificationService, "awsBaseUrl", "http://localhost:4566");
        ReflectionTestUtils.setField(pushNotificationService, "awsRegion", "us-east-1");
        ReflectionTestUtils.setField(pushNotificationService, "lambdaFunctionName", "push-notification-lambda");

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
    }

    @Test
    void saveSubscriptionGenerateIdAndSaveData() {
        String userId = "testUser";
        when(pushSubscriptionRepository.save(any(PushSubscription.class))).thenReturn(pushSubscription);

        PushSubscription result = pushNotificationService.saveSubscription(subscriptionDto, userId);

        verify(pushSubscriptionRepository).save(subscriptionArgumentCaptor.capture());
        PushSubscription capturedSubscription = subscriptionArgumentCaptor.getValue();

        assertNotNull(capturedSubscription.getSubscriptionId(), "Subscription ID should be generated");
        assertEquals(userId, capturedSubscription.getUserId(), "User ID should be set correctly");
        assertEquals(subscriptionDto.endpoint(), capturedSubscription.getEndpoint(), "Endpoint should match");
        assertEquals(subscriptionDto.expirationTime(), capturedSubscription.getExpirationTime(), "Expiration time should match");
        assertEquals(subscriptionDto.keys(), capturedSubscription.getKeys(), "Keys should match");
        assertEquals(pushSubscription, result, "Return value should be the saved subscription");
    }

    @Test
    void sendNotificationShouldProcessAllSubscriptions() throws JsonProcessingException {
        String testMessage = "Test notification message";
        PushSubscription subscription1 = pushSubscription;
        PushSubscription subscription2 = PushSubscription.builder()
            .subscriptionId("SUBSCRIPTION#5678")
            .userId("otherUser")
            .endpoint("https://example2.com/endpoint")
            .expirationTime(null)
            .keys(Map.of("p256dh", "otherKey1", "auth", "otherKey2"))
            .build();

        when(pushSubscriptionRepository.findAll()).thenReturn(Arrays.asList(subscription1, subscription2));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"test\":\"payload\"}");

        pushNotificationService.sendNotification(testMessage);

        verify(pushSubscriptionRepository).findAll();
        verify(objectMapper, times(2)).writeValueAsString(any());
    }

    @Test
    void sendNotificationHandlesEmptySubscriptionList() {
        when(pushSubscriptionRepository.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pushNotificationService.sendNotification("Test message");
        });

        assertEquals("No subscriptions found", exception.getMessage());
    }

    @Test
    void sendNotificationSkipInvalidSubscriptions() throws JsonProcessingException {
        PushSubscription validSubscription = pushSubscription;
        PushSubscription invalidSubscription = PushSubscription.builder()
            .subscriptionId("INVALID#1234")
            .userId("invalidUser")
            .endpoint(null)
            .keys(Map.of("p256dh", "key1", "auth", "key2"))
            .build();

        when(pushSubscriptionRepository.findAll()).thenReturn(Arrays.asList(validSubscription, invalidSubscription));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"test\":\"payload\"}");

        pushNotificationService.sendNotification("Test message");

        verify(pushSubscriptionRepository).findAll();
        verify(objectMapper, times(1)).writeValueAsString(any());
    }
}
