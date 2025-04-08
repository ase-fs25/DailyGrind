package com.uzh.ase.dailygrind.mspushnotifications.pushNotification.repository;

import com.uzh.ase.dailygrind.mspushnotifications.pushNotification.repository.entity.PushSubscription;
import lombok.NonNull;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

import java.util.List;

@EnableScan
public interface PushSubscriptionRepository extends DynamoDBCrudRepository<PushSubscription, String>{
    @NonNull List<PushSubscription> findAll();
}
