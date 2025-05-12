package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Map;
import java.util.UUID;

/**
 * Represents a push notification subscription for a user.
 * <p>
 * This class is used to map push notification subscription details for users to a DynamoDB table.
 * It includes information about the user, the subscription endpoint, the expiration time, and the keys associated with the subscription.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscription {

    /**
     * Unique identifier for the subscription.
     * <p>
     * The subscription ID is generated as a UUID if not provided.
     */
    private String subscriptionId;

    /**
     * Gets the subscription ID.
     * <p>
     * This method is annotated as the partition key for DynamoDB, ensuring that the subscription is uniquely identifiable.
     *
     * @return the subscription ID
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("subscription_id")
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * The unique identifier of the user associated with the subscription.
     * <p>
     * This user ID links the subscription to a specific user.
     */
    private String userId;

    /**
     * The endpoint URL for the push notification service.
     * <p>
     * This URL is where push notifications will be sent.
     */
    private String endpoint;

    /**
     * The expiration time of the subscription.
     * <p>
     * This timestamp indicates when the subscription will expire.
     */
    private Long expirationTime;

    /**
     * A map of keys associated with the subscription.
     * <p>
     * These keys are used for the push notification subscription process.
     */
    private Map<String, String> keys;

    /**
     * Generates a new subscription ID if it is not already set.
     * <p>
     * This method assigns a UUID to the `subscriptionId` if it is null.
     */
    public void generateId() {
        if(this.subscriptionId == null){
            this.subscriptionId = UUID.randomUUID().toString();
        }
    }

}
