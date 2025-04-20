package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.service;


import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.PushSubscriptionRepository;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PushNotificationService {
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;

    private final String publicKey = "BGNKMIqVDc7udZPZ8manv9UF7uzQtCaYJvzEEe7rr6zor3HPkFuPTN5q1cUoABwYR-Dwa5Fwhx0BUOImZJC-rG8";
    private final String privateKey = "1tyL-Kw64dsVRGE7uUew77koormjIC67lu-jbTLWW0k";
    private final String subject = "mailto:tim.vorburger@uzh.ch";

//    private PushService pushService;

//    @PostConstruct
//    public void init() throws Exception {
//        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
//            Security.addProvider(new BouncyCastleProvider());
//        }
//        pushService = new PushService(publicKey, privateKey, subject);
//    }


    public PushSubscription saveSubscription(SubscriptionDto pushSubscription, String userId) {
        PushSubscription subscription = PushSubscription.builder()
                .pushSubscription(pushSubscription)
                .userId(userId)
                .build();

        return pushSubscriptionRepository.save(subscription);
    }

//    public void sendNotification(String message) {
//        List<PushSubscription> subscriptions = pushSubscriptionRepository.findAll();
//        if(subscriptions == null || subscriptions.isEmpty()) {
//            throw new RuntimeException("No subscriptions found");
//        }
//
//        System.out.println("Found " + subscriptions.size() + " subscriptions");
//
//        for(PushSubscription subscription : subscriptions) {
//            try {
//                System.out.println("Processing subscription ID: " + subscription.getSubscriptionId());
//
//                SubscriptionDto subscriptionDto = subscription.getPushSubscription();
//                if(subscriptionDto == null) {
//                    System.out.println("Warning: subscription data is null for user: " + subscription.getUserId());
//                    continue; // Skip this subscription and move to the next one
//                }
//
//                // Check that required fields exist
//                if(subscriptionDto.endpoint() == null || subscriptionDto.keys() == null) {
//                    System.out.println("Warning: subscription missing required fields for user: " + subscription.getUserId());
//                    continue;
//                }
//
//                if(!subscriptionDto.keys().containsKey("p256dh") || !subscriptionDto.keys().containsKey("auth")) {
//                    System.out.println("Warning: subscription keys missing required values for user: " + subscription.getUserId());
//                    continue;
//                }
//
//                // Create a web-push library Subscription object
//                Subscription pushSubscription = new Subscription(
//                        subscriptionDto.endpoint(),
//                        new Subscription.Keys(
//                                subscriptionDto.keys().get("p256dh"),
//                                subscriptionDto.keys().get("auth")
//                        )
//                );
//
//                System.out.println("This is the pushSubscription object we send: " + objectMapper.writeValueAsString(pushSubscription));
//
//                Map<String, String> payload = new HashMap<>();
//                payload.put("title", "New Notification");
//                payload.put("body", message.replaceAll("^\"|\"$", ""));
//                payload.put("timestamp", String.valueOf(System.currentTimeMillis()));
//                String jsonPayload = objectMapper.writeValueAsString(payload);
//                byte[] payloadBytes = jsonPayload.getBytes(StandardCharsets.UTF_8);
//
//                Notification notification = new Notification(pushSubscription, jsonPayload);
//
//
//                try {
//                    HttpResponse httpResponse = pushService.send(notification);
//                    int statusCode = httpResponse.getStatusLine().getStatusCode();
//                    System.out.println("Push service response code: " + statusCode);
//
//                    if (statusCode >= 400) {
//                        String responseBody = EntityUtils.toString(httpResponse.getEntity());
//                        System.out.println("Error response: " + responseBody);
//                    }
//                } catch (Exception e) {
//                    System.out.println("Exception during send: " + e.getClass().getName() + ": " + e.getMessage());
//                    e.printStackTrace();
//                }
//
//                System.out.println("Notification sent successfully");
//
//            } catch (Exception e) {
//                System.out.println("Error sending notification: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }


//    send with lambda function
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

            // Prepare the payload for the Lambda function
            Map<String, Object> payload = new HashMap<>();
            payload.put("subscription", subscriptionDto);
            payload.put("message", new HashMap<String, String>(){{
                put("title", "New Notification");
                put("body", message.replaceAll("^\"|\"$", ""));
                put("timestamp", String.valueOf(System.currentTimeMillis()));
            }});

            // Convert payload to JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // Call Lambda function
            invokeNotificationLambda(jsonPayload);

        } catch (Exception e) {
            System.out.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
            }
        }
    }

    private void invokeNotificationLambda(String payload) {
        try {
            // Create Lambda client for LocalStack
            String endpoint = "http://localhost.localstack.cloud:4566";
            String region = "us-east-1";
            String functionName = "pushNotificationLambda";

            LambdaClient lambdaClient = LambdaClient.builder()
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(region))
                    .build();

            // Create invoke request
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build();

            // Invoke Lambda function
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
