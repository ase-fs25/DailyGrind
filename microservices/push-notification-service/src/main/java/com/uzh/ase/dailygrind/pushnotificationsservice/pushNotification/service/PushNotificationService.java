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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {
    @Value("${dg.us.aws.base-url}")
    private String awsBaseUrl;

    @Value("${dg.us.aws.region}")
    private String awsRegion;

    @Value("${dg.lambda.function-name}")
    private String lambdaFunctionName;

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;

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
                put("body", message.replaceAll("^\"|\"$", ""));
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
            String endpoint = awsBaseUrl;
            String region = awsRegion;
            String functionName = lambdaFunctionName;

            LambdaClient lambdaClient = LambdaClient.builder()
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(region))
                    .build();

            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build();

            InvokeResponse response = lambdaClient.invoke(request);
            int statusCode = response.statusCode();
            String lambdaResponse = new String(response.payload().asByteArray());

            log.info("Lambda response: {}", lambdaResponse);

            if (statusCode >= 200 && statusCode < 300) {
                log.info("Push notification Lambda invoked successfully");
            } else {
                log.warn("Failed to invoke Lambda, status: {}", statusCode);
                if (response.functionError() != null) {
                    log.warn("Lambda error: {}",response.functionError());
                }
            }
        } catch (Exception e) {
            log.error("Exception invoking Lambda: {}", e.getMessage());
        }
    }
}
