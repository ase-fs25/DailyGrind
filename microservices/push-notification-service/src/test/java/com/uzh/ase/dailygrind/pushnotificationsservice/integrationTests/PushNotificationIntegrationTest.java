package com.uzh.ase.dailygrind.pushnotificationsservice.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.pushnotificationsservice.config.*;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({LocalStackTestConfig.class, AwsTestCredentialsConfig.class, DynamoDBTestConfig.class, LambdaTestConfig.class})
public class PushNotificationIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    DynamoDbTable<PushSubscription> pushSubscriptionTable;

    @Autowired
    private LambdaClient lambdaClient;

    @BeforeEach
    void setUp() {
        reset(lambdaClient);
    }

    @AfterEach
    void tearDown() {
        pushSubscriptionTable.scan().items().forEach(pushSubscriptionTable::deleteItem);
    }

    @Nested
    class SubscribeTests {

        @Test
        @WithMockUser(username = "test-user-id")
        void shouldCreateSubscription() throws Exception {
            // Given
            Map<String, String> keys = new HashMap<>();
            keys.put("p256dh", "test-p256dh-key");
            keys.put("auth", "test-auth-key");

            SubscriptionDto subscriptionDto = new SubscriptionDto(
                "https://fcm.googleapis.com/fcm/send/" + UUID.randomUUID(),
                null,
                keys
            );

            // When & Then
            String response = mockMvc.perform(post("/push-notifications/subscribe")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subscriptionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.endpoint").value(subscriptionDto.endpoint()))
                .andExpect(jsonPath("$.keys.p256dh").value("test-p256dh-key"))
                .andExpect(jsonPath("$.keys.auth").value("test-auth-key"))
                .andExpect(jsonPath("$.userId").value("test-user-id"))
                    .andReturn().getResponse().getContentAsString();

            String subscriptionId = objectMapper.readTree(response).get("subscriptionId").asText();

            mockMvc.perform(get("/push-notifications/" + subscriptionId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoint").value(subscriptionDto.endpoint()));
        }

        @Test
        @WithMockUser(username = "test-user-id")
        void shouldRejectInvalidSubscription() throws Exception {
            // Given
            SubscriptionDto subscriptionDto = new SubscriptionDto(
                null,
                null,
                null
            );

            // When & Then
            mockMvc.perform(post("/push-notifications/subscribe")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subscriptionDto)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class SendNotificationTests {

        @Test
        @WithMockUser(username = "test-admin", roles = {"ADMIN"})
        void shouldSendNotificationsToAllSubscribers() throws Exception {
            // Setup
            Map<String, String> keys1 = new HashMap<>();
            keys1.put("p256dh", "test-p256dh-key-1");
            keys1.put("auth", "test-auth-key-1");
            SubscriptionDto subscription1 = new SubscriptionDto(
                "https://example.com/endpoint1",
                null,
                keys1
            );

            Map<String, String> keys2 = new HashMap<>();
            keys2.put("p256dh", "test-p256dh-key-2");
            keys2.put("auth", "test-auth-key-2");
            SubscriptionDto subscription2 = new SubscriptionDto(
                "https://example.com/endpoint2",
                null,
                keys2
            );

            // Create test subscriptions
            mockMvc.perform(post("/push-notifications/subscribe")
                    .with(SecurityMockMvcRequestPostProcessors.user("user-1"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subscription1)))
                .andExpect(status().isCreated());

            mockMvc.perform(post("/push-notifications/subscribe")
                    .with(SecurityMockMvcRequestPostProcessors.user("user-2"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subscription2)))
                .andExpect(status().isCreated());

            // Mock response
            InvokeResponse invokeResponse = InvokeResponse.builder()
                .statusCode(200)
                .payload(SdkBytes.fromUtf8String("{}"))
                .build();
            doReturn(invokeResponse).when(lambdaClient).invoke(any(InvokeRequest.class));

            // Send notification
            Map<String, String> notification = new HashMap<>();
            notification.put("title", "Test Title");
            notification.put("body", "Test notification message");

            mockMvc.perform(post("/push-notifications/send")
                    .with(SecurityMockMvcRequestPostProcessors.user("test-admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk());

            // Verify Lambda was invoked
            verify(lambdaClient, times(2)).invoke(any(InvokeRequest.class));
        }

        @Test
        @WithMockUser(username = "test-admin", roles = {"ADMIN"})
        void shouldHandleEmptySubscriptionList() throws Exception {
            // Given - ensure no subscriptions exist
            pushSubscriptionTable.scan().items().forEach(pushSubscriptionTable::deleteItem);

            // Create a proper JSON payload
            Map<String, String> notification = new HashMap<>();
            notification.put("title", "Test Title");
            notification.put("body", "Test notification for nobody");
            String notificationJson = objectMapper.writeValueAsString(notification);

            // When & Then
            mockMvc.perform(post("/push-notifications/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(notificationJson))
                .andExpect(status().isOk());

            // No Lambda calls should happen
            Mockito.verify(lambdaClient, never()).invoke(ArgumentMatchers.any(InvokeRequest.class));
        }

    }

}
