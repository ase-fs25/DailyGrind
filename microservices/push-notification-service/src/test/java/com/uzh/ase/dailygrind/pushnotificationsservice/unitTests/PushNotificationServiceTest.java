package com.uzh.ase.dailygrind.pushnotificationsservice.unitTests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.PushSubscriptionRepository;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service.PushNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PushNotificationServiceTest {
    @Mock
    private PushSubscriptionRepository pushSubscriptionRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private LambdaClient lambdaClient;

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

        InvokeResponse mockResponse = InvokeResponse.builder()
                .payload(SdkBytes.fromUtf8String("{\"success\":true}"))
                .build();
        when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(mockResponse);
    }

    @Test
    void saveSubscriptionGenerateIdAndSaveData() {
        // Given
        String userId = "testUser";
        when(pushSubscriptionRepository.save(any(PushSubscription.class))).thenReturn(pushSubscription);

        // When
        PushSubscription result = pushNotificationService.saveSubscription(subscriptionDto, userId);

        // Then
        verify(pushSubscriptionRepository).save(subscriptionArgumentCaptor.capture());
        PushSubscription capturedSubscription = subscriptionArgumentCaptor.getValue();

        assertNotNull(capturedSubscription.getSubscriptionId());
        assertEquals(userId, capturedSubscription.getUserId());
        assertEquals(subscriptionDto.endpoint(), capturedSubscription.getEndpoint());
        assertEquals(subscriptionDto.expirationTime(), capturedSubscription.getExpirationTime());
        assertEquals(subscriptionDto.keys(), capturedSubscription.getKeys());
        assertEquals(pushSubscription, result);
    }

    @Test
    void saveSubscriptionShouldReturnNullForNullEndpoint() {
        // Given
        String userId = "testUser";
        SubscriptionDto invalidDto = new SubscriptionDto(
            null,
            null,
            Map.of("p256dh", "key1", "auth", "key2")
        );

        // When
        PushSubscription result = pushNotificationService.saveSubscription(invalidDto, userId);

        // Then
        assertNull(result);
        verify(pushSubscriptionRepository, never()).save(any());
    }

    @Test
    void saveSubscriptionShouldReturnNullForNullKeys() {
        // Given
        String userId = "testUser";
        SubscriptionDto invalidDto = new SubscriptionDto(
            "https://example.com/endpoint",
            null,
            null
        );

        // When
        PushSubscription result = pushNotificationService.saveSubscription(invalidDto, userId);

        // Then
        assertNull(result);
        verify(pushSubscriptionRepository, never()).save(any());
    }

    @Test
    void saveSubscriptionShouldReturnNullForMissingRequiredKeys() {
        // Given
        String userId = "testUser";
        SubscriptionDto invalidDto = new SubscriptionDto(
            "https://example.com/endpoint",
            null,
            Map.of("someKey", "someValue")
        );

        // When
        PushSubscription result = pushNotificationService.saveSubscription(invalidDto, userId);

        // Then
        assertNull(result);
        verify(pushSubscriptionRepository, never()).save(any());
    }

    @Test
    void sendNotificationShouldInvokeLambdaForEachSubscription() throws JsonProcessingException {
        // Given
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

        // When
        pushNotificationService.sendNotification(testMessage);

        // Then
        verify(pushSubscriptionRepository).findAll();
        verify(objectMapper, times(2)).writeValueAsString(any());
        verify(lambdaClient, times(2)).invoke(invokeRequestArgumentCaptor.capture());

        List<InvokeRequest> capturedRequests = invokeRequestArgumentCaptor.getAllValues();
        assertEquals(2, capturedRequests.size());

        for (InvokeRequest request : capturedRequests) {
            assertEquals("push-notification-lambda", request.functionName());
            assertNotNull(request.payload());
        }
    }

    @Test
    void sendNotificationHandlesEmptySubscriptionList() {
        // Set up log capture
        Logger serviceLogger = (Logger) LoggerFactory.getLogger(PushNotificationService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        serviceLogger.addAppender(listAppender);

        try {
            // When
            when(pushSubscriptionRepository.findAll()).thenReturn(Collections.emptyList());
            pushNotificationService.sendNotification("Test message");

            // Then
            List<ILoggingEvent> loggedEvents = listAppender.list;
            assertEquals(1, loggedEvents.size());
            assertEquals(Level.ERROR, loggedEvents.get(0).getLevel());
            assertEquals("No subscriptions found", loggedEvents.get(0).getFormattedMessage());
            verify(lambdaClient, never()).invoke(any(InvokeRequest.class));
        } finally {
            // Clean up
            serviceLogger.detachAppender(listAppender);
        }
    }

    @Test
    void sendNotificationSkipsInvalidSubscriptionsButProcessesValid() throws JsonProcessingException {
        // Given
        PushSubscription validSubscription = pushSubscription;
        PushSubscription invalidSubscription = PushSubscription.builder()
                .subscriptionId("INVALID#1234")
                .userId("invalidUser")
                .endpoint(null)
                .keys(Map.of("p256dh", "key1", "auth", "key2"))
                .build();

        when(pushSubscriptionRepository.findAll()).thenReturn(Arrays.asList(validSubscription, invalidSubscription));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"test\":\"payload\"}");

        // When
        pushNotificationService.sendNotification("Test message");

        // Then
        verify(pushSubscriptionRepository).findAll();
        verify(objectMapper, times(1)).writeValueAsString(any());
        verify(lambdaClient, times(1)).invoke(any(InvokeRequest.class));
    }

    @Test
    void lambdaInvocationHandlesExceptions() throws JsonProcessingException {
        // Given
        when(pushSubscriptionRepository.findAll()).thenReturn(Collections.singletonList(pushSubscription));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"test\":\"payload\"}");
        when(lambdaClient.invoke(any(InvokeRequest.class))).thenThrow(new RuntimeException("Lambda invocation failed"));

        // When/Then
        assertDoesNotThrow(() -> pushNotificationService.sendNotification("Test message"));
        verify(lambdaClient).invoke(any(InvokeRequest.class));
    }
}
