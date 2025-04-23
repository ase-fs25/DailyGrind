package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service;


import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.PushSubscriptionRepository;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class PushNotificationService {
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;

    public PushSubscription saveSubscription(SubscriptionDto pushSubscription, String userId) {
        PushSubscription subscription = PushSubscription.builder()
                .pushSubscription(pushSubscription)
                .userId(userId)
                .build();

        return pushSubscriptionRepository.save(subscription);
    }

//    send with push-notification-lambda
public void sendNotification(String message) {
    List<PushSubscription> subscriptions = pushSubscriptionRepository.findAll();
    if(subscriptions == null || subscriptions.isEmpty()) {
        throw new RuntimeException("No subscriptions found");
    }

    System.out.println("Found " + subscriptions.size() + " subscriptions");

    for(PushSubscription subscription : subscriptions) {
        try {
            System.out.println("Processing subscription ID: " + subscription.getSubscriptionId());

            SubscriptionDto subscriptionDto = subscription.getPushSubscription();
            if(subscriptionDto == null || subscriptionDto.endpoint() == null || subscriptionDto.keys() == null) {
                System.out.println("Warning: invalid subscription for user: " + subscription.getUserId());
                continue;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("subscription", subscriptionDto);
            payload.put("message", new HashMap<String, String>(){{
                put("title", "New Notification");
                put("body", message.replaceAll("^\"|\"$", ""));
                put("timestamp", String.valueOf(System.currentTimeMillis()));
            }});

            String jsonPayload = objectMapper.writeValueAsString(payload);

            invokeNotificationLambda(jsonPayload);

        } catch (Exception e) {
            System.out.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
            }
        }
    }

    private void invokeNotificationLambda(String payload) {
        try {
            String endpoint = "http://localhost.localstack.cloud:4566";
            String region = "us-east-1";
            String functionName = "pushNotificationLambda";

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
            String lambdaRespone = new String(response.payload().asByteArray());
            System.out.println("Lambda response: " + lambdaRespone);

            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("Push notification Lambda invoked successfully");
            } else {
                System.out.println("Failed to invoke Lambda, status: " + statusCode);
                if (response.functionError() != null) {
                    System.out.println("Error: " + response.functionError());
                }
                if (response.payload() != null) {
                    System.out.println("Response: " + response.payload().asUtf8String());
                }
            }
        } catch (Exception e) {
            System.out.println("Exception invoking Lambda: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
