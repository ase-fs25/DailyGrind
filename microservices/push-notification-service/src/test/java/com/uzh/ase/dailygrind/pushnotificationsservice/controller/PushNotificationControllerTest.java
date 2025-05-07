package com.uzh.ase.dailygrind.pushnotificationsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.PushNotificationController;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service.PushNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.security.Principal;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PushNotificationControllerTest {

    @Mock
    private PushNotificationService pushNotificationService;

    @InjectMocks
    private PushNotificationController pushNotificationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private SubscriptionDto subscriptionDto;
    private PushSubscription savedSubscription;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(pushNotificationController).build();
        objectMapper = new ObjectMapper();

        subscriptionDto = new SubscriptionDto(
            "https://example.com/endpoint",
            123456789L,
            Map.of("p256dh", "key1", "auth", "key2")
        );

        savedSubscription = PushSubscription.builder()
            .subscriptionId("SUBSCRIPTION#1234")
            .userId("testUser")
            .endpoint(subscriptionDto.endpoint())
            .expirationTime(subscriptionDto.expirationTime())
            .keys(subscriptionDto.keys())
            .build();
    }

    @Test
    void subscribeReturnsCreatedAndSavesSubscription() throws Exception {
        when(pushNotificationService.saveSubscription(any(SubscriptionDto.class), eq("testUser")))
            .thenReturn(savedSubscription);

        mockMvc.perform(post("/push-notifications/subscribe")
                .with(request -> {
                    request.setUserPrincipal(mock(Principal.class));
                    when(request.getUserPrincipal().getName()).thenReturn("testUser");
                    return request;
                })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscriptionDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.subscriptionId").value("SUBSCRIPTION#1234"))
            .andExpect(jsonPath("$.userId").value("testUser"))
            .andExpect(jsonPath("$.endpoint").value("https://example.com/endpoint"));

        verify(pushNotificationService).saveSubscription(any(SubscriptionDto.class), eq("testUser"));
    }

    @Test
    void sendNotificationReturnsOkWhenNotificationSent() throws Exception {
        doNothing().when(pushNotificationService).sendNotification("Test notification message");

        mockMvc.perform(post("/push-notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"Test notification message\""))
            .andExpect(status().isOk());

        verify(pushNotificationService).sendNotification("\"Test notification message\"");
    }
}
