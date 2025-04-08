package com.uzh.ase.dailygrind.mspushnotifications.pushNotification.service;

import com.uzh.ase.dailygrind.mspushnotifications.pushNotification.repository.PushSubscriptionRepository;
import com.uzh.ase.dailygrind.mspushnotifications.pushNotification.repository.entity.PushSubscription;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Security;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushNotificationService {
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;

    private final String publicKey = "BGNKMIqVDc7udZPZ8manv9UF7uzQtCaYJvzEEe7rr6zor3HPkFuPTN5q1cUoABwYR-Dwa5Fwhx0BUOImZJC-rG8";
    private final String privateKey = "1tyL-Kw64dsVRGE7uUew77koormjIC67lu-jbTLWW0k";
    private final String subject = "mailto:tim.vorburger@uzh.ch";

    private PushService pushService;

    @PostConstruct
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(publicKey, privateKey, subject);
    }

    public List<PushSubscription> getAllSubscriptions() {
        return pushSubscriptionRepository.findAll();
    }

    public PushSubscription saveSubscription(PushSubscription pushSubscription) {
        return pushSubscriptionRepository.save(pushSubscription);
    }

    public void sendNotification(String message) {
        List<PushSubscription> subscriptions = pushSubscriptionRepository.findAll();

        if(subscriptions.isEmpty()) {
            return;
        }

        for(PushSubscription subscription : subscriptions) {
            try {
                // Parse the JSON string from your entity
                JsonNode subscriptionJson = objectMapper.readTree(subscription.getPushSubscription());

                // Create a web-push library Subscription object
                Subscription pushSubscription = new Subscription(
                        subscriptionJson.get("endpoint").asText(),
                        new Subscription.Keys(
                                subscriptionJson.get("keys").get("p256dh").asText(),
                                subscriptionJson.get("keys").get("auth").asText()
                        )
                );

                Notification notification = new Notification(pushSubscription, "this is the message body");
                pushService.send(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
