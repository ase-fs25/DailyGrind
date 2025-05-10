package com.uzh.ase.dailygrind.pushnotificationsservice.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service.PushNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PushNotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PushNotificationService pushNotificationService;

    @Test
    @WithMockUser(username = "test-user")
    void shouldSubscribeToPushNotifications() throws Exception {
        // Given
        SubscriptionDto subscriptionDto = new SubscriptionDto(
            "https://example.com/endpoint",
            null,
            Map.of("p256dh", "key1", "auth", "key2")
        );

        PushSubscription mockResponse = PushSubscription.builder()
            .subscriptionId(UUID.randomUUID().toString())
            .userId("test-user")
            .endpoint(subscriptionDto.endpoint())
            .expirationTime(subscriptionDto.expirationTime())
            .keys(subscriptionDto.keys())
            .build();

        when(pushNotificationService.saveSubscription(any(SubscriptionDto.class), eq("test-user")))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/push-notifications/subscribe")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscriptionDto)))
            .andExpect(status().isCreated());

        verify(pushNotificationService).saveSubscription(any(SubscriptionDto.class), eq("test-user"));
    }

    @Test
    @WithMockUser(username = "admin-user")
    void shouldSendNotifications() throws Exception {
        // Given
        String message = "Test notification";

        // When & Then
        mockMvc.perform(post("/push-notifications/send")
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content(message))
            .andExpect(status().isOk());

        verify(pushNotificationService).sendNotification(message);
    }

}
