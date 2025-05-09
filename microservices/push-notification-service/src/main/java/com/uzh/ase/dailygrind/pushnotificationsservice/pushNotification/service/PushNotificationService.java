package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service;

import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.PushSubscriptionRepository;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    @Value("${dg.lambda.function-name}")
    private String lambdaFunctionName;

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;
    private final LambdaClient lambdaClient;

    public PushSubscription saveSubscription(SubscriptionDto pushSubscription, String userId) {
        PushSubscription subscription = PushSubscription.builder()
            .userId(userId)
            .endpoint(pushSubscription.endpoint())
            .expirationTime(pushSubscription.expirationTime())
            .keys(pushSubscription.keys())
            .build();

        subscription.generateId();
        return pushSubscriptionRepository.save(subscription);
    }

public void sendNotification(String message) {
    List<PushSubscription> subscriptions = pushSubscriptionRepository.findAll();
    if(subscriptions == null || subscriptions.isEmpty()) {
        throw new RuntimeException("No subscriptions found");
    }

    for(PushSubscription subscription : subscriptions) {
        try {
            log.info("Processing subscription ID: {}", subscription.getSubscriptionId());

            SubscriptionDto subscriptionDto = new SubscriptionDto(
                subscription.getEndpoint(),
                subscription.getExpirationTime(),
                subscription.getKeys()
            );
            if(subscriptionDto.endpoint() == null || subscriptionDto.keys() == null) {
                log.warn("Warning: invalid subscription for user: {}",subscription.getUserId());
                continue;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("subscription", subscriptionDto);
            payload.put("message", new HashMap<String, String>(){{
                put("title", "DailyGrind Reminder");
                put("body", message);
                put("timestamp", String.valueOf(System.currentTimeMillis()));
            }});

            String jsonPayload = objectMapper.writeValueAsString(payload);

            invokeNotificationLambda(jsonPayload);

        } catch (Exception e) {
            log.error("Error sending notification for user: {}",subscription.getUserId(), e);
            }
        }
    }

    private void invokeNotificationLambda(String payload) {
        try {
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(lambdaFunctionName)
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build();

            InvokeResponse response = lambdaClient.invoke(request);
            log.info("Lambda response: {}", response.payload().asByteArray());

        } catch (Exception e) {
            log.error("Exception invoking Lambda: {}", e.getMessage());
        }
    }
}
