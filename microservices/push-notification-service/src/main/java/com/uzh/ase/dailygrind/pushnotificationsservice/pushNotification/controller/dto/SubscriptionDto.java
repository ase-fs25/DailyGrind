package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto;

import java.util.Map;

/**
 * Data Transfer Object (DTO) for representing a push notification subscription.
 * <p>
 * This class is used for transferring data related to a push notification subscription, such as the endpoint URL,
 * the expiration time, and the keys associated with the subscription. It serves as a lightweight object for
 * communication between layers in the application.
 */
public record SubscriptionDto(
    /**
     * The endpoint URL where the push notification should be sent.
     * <p>
     * This URL represents the destination endpoint for the push notification subscription.
     */
    String endpoint,

    /**
     * The expiration time of the subscription in milliseconds since epoch.
     * <p>
     * This time indicates when the subscription will expire and no longer be valid for push notifications.
     */
    Long expirationTime,

    /**
     * A map of keys associated with the push notification subscription.
     * <p>
     * The map contains key-value pairs that can represent various authentication or encryption keys needed
     * for securing the subscription or sending notifications.
     */
    Map<String, String> keys
) {
}
