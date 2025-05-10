package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository;

import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PushSubscriptionRepository {

    private final DynamoDbTable<PushSubscription> pushSubscriptionTable;


    public PushSubscription save(PushSubscription subscription) {
        pushSubscriptionTable.putItem(subscription);
        return subscription;
    }

    public List<PushSubscription> findAll() {
        return pushSubscriptionTable.scan().items().stream().toList();
    }

    public Optional<PushSubscription> findById(String subscriptionId) {
        PushSubscription subscription = pushSubscriptionTable.getItem(
            Key.builder().partitionValue(subscriptionId).build()
        );
        return Optional.ofNullable(subscription);
    }

    public void deleteById(String subscriptionId) {
        pushSubscriptionTable.deleteItem(
            Key.builder().partitionValue(subscriptionId).build()
        );
    }
}
