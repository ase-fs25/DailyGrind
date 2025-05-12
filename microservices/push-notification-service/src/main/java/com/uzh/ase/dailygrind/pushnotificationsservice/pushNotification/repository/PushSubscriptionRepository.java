package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository;

import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;
import java.util.Optional;

/**
 * Repository class for interacting with the PushSubscription DynamoDB table.
 * <p>
 * This repository handles CRUD operations for PushSubscription entities stored in DynamoDB.
 */
@Repository
@RequiredArgsConstructor
public class PushSubscriptionRepository {

    /**
     * DynamoDB table for storing push subscriptions.
     */
    private final DynamoDbTable<PushSubscription> pushSubscriptionTable;

    /**
     * Saves a push notification subscription to the DynamoDB table.
     * <p>
     * This method persists the given PushSubscription object in the DynamoDB table.
     *
     * @param subscription the PushSubscription entity to save
     * @return the saved PushSubscription entity
     */
    public PushSubscription save(PushSubscription subscription) {
        pushSubscriptionTable.putItem(subscription);
        return subscription;
    }

    /**
     * Retrieves all push notification subscriptions from the DynamoDB table.
     * <p>
     * This method scans the entire PushSubscription table and returns a list of all subscriptions.
     *
     * @return a list of all PushSubscription entities
     */
    public List<PushSubscription> findAll() {
        return pushSubscriptionTable.scan().items().stream().toList();
    }

    /**
     * Retrieves a push notification subscription by its ID.
     * <p>
     * This method looks up the subscription with the given subscriptionId from the DynamoDB table.
     *
     * @param subscriptionId the ID of the PushSubscription to retrieve
     * @return an Optional containing the found PushSubscription or an empty Optional if not found
     */
    public Optional<PushSubscription> findById(String subscriptionId) {
        PushSubscription subscription = pushSubscriptionTable.getItem(
            Key.builder().partitionValue(subscriptionId).build()
        );
        return Optional.ofNullable(subscription);
    }

    /**
     * Deletes a push notification subscription by its ID.
     * <p>
     * This method deletes the subscription with the given subscriptionId from the DynamoDB table.
     *
     * @param subscriptionId the ID of the PushSubscription to delete
     */
    public void deleteById(String subscriptionId) {
        pushSubscriptionTable.deleteItem(
            Key.builder().partitionValue(subscriptionId).build()
        );
    }
}
