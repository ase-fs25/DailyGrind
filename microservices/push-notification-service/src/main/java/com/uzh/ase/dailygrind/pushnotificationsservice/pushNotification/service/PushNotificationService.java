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
import java.util.Optional;

/**
 * Service class for managing push notifications.
 * <p>
 * This service handles the creation of push notification subscriptions, sending push notifications to all subscribed users,
 * and interacting with AWS Lambda for notification delivery.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    /**
     * The Lambda function name for sending push notifications.
     */
    @Value("${dg.lambda.function-name}")
    private String lambdaFunctionName;

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;
    private final LambdaClient lambdaClient;

    /**
     * Saves a new push notification subscription.
     * <p>
     * This method validates the subscription details (endpoint and keys) before saving the subscription to DynamoDB.
     *
     * @param pushSubscription the subscription details
     * @param userId the user ID for the subscription
     * @return the saved PushSubscription entity, or null if the subscription was invalid
     */
    public PushSubscription saveSubscription(SubscriptionDto pushSubscription, String userId) {
        if (pushSubscription.endpoint() == null || pushSubscription.keys() == null) {
            log.error("Invalid subscription: endpoint or keys are null for user {}", userId);
            return null;
        }

        if (!pushSubscription.keys().containsKey("p256dh") || !pushSubscription.keys().containsKey("auth")) {
            log.error("Invalid subscription: missing required keys p256dh or auth for user {}", userId);
            return null;
        }

        PushSubscription subscription = PushSubscription.builder()
            .userId(userId)
            .endpoint(pushSubscription.endpoint())
            .expirationTime(pushSubscription.expirationTime())
            .keys(pushSubscription.keys())
            .build();

        subscription.generateId();
        return pushSubscriptionRepository.save(subscription);
    }

    /**
     * Sends a push notification to all subscribed users.
     * <p>
     * This method iterates over all active push subscriptions and invokes an AWS Lambda function to send the notification.
     *
     * @param message the message to send in the push notification
     */
    public void sendNotification(String message) {
        List<PushSubscription> subscriptions = pushSubscriptionRepository.findAll();
        if (subscriptions == null || subscriptions.isEmpty()) {
            log.error("No subscriptions found");
        }

        for (PushSubscription subscription : subscriptions) {
            try {
                log.info("Processing subscription ID: {}", subscription.getSubscriptionId());

                SubscriptionDto subscriptionDto = new SubscriptionDto(
                    subscription.getEndpoint(),
                    subscription.getExpirationTime(),
                    subscription.getKeys()
                );
                if (subscriptionDto.endpoint() == null || subscriptionDto.keys() == null) {
                    log.warn("Warning: invalid subscription for user: {}", subscription.getUserId());
                    continue;
                }

                Map<String, Object> payload = new HashMap<>();
                payload.put("subscription", subscriptionDto);
                payload.put("message", new HashMap<String, String>() {{
                    put("title", "DailyGrind Reminder");
                    put("body", message);
                    put("timestamp", String.valueOf(System.currentTimeMillis()));
                }});

                String jsonPayload = objectMapper.writeValueAsString(payload);

                invokeNotificationLambda(jsonPayload);

            } catch (Exception e) {
                log.error("Error sending notification for user: {}", subscription.getUserId(), e);
            }
        }
    }

    /**
     * Deletes a push notification subscription by its ID.
     * <p>
     * This method removes the subscription from DynamoDB based on the provided subscription ID.
     *
     * @param subscriptionId the ID of the subscription to delete
     */
    public void deleteSubscription(String subscriptionId) {
        pushSubscriptionRepository.deleteById(subscriptionId);
    }

    /**
     * Retrieves a push notification subscription by its ID.
     * <p>
     * This method fetches the subscription with the given ID from DynamoDB.
     *
     * @param subscriptionId the ID of the subscription to retrieve
     * @return an Optional containing the found PushSubscription or an empty Optional if not found
     */
    public Optional<PushSubscription> getSubscriptionById(String subscriptionId) {
        return pushSubscriptionRepository.findById(subscriptionId);
    }

    /**
     * Invokes the AWS Lambda function to send a push notification.
     * <p>
     * This method creates an InvokeRequest, passing the payload to the Lambda function for processing.
     *
     * @param payload the JSON payload to send to the Lambda function
     */
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
