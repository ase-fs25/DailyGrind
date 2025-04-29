package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository;

import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity.PushSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

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
}
