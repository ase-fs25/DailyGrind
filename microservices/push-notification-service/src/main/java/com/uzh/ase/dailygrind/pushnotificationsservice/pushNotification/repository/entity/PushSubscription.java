package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Map;
import java.util.UUID;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscription {


    private String subscriptionId;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("subscription_id")
    public String getSubscriptionId() {
        return subscriptionId;
    }

    private String userId;

    private String endpoint;

    Long expirationTime;

    Map<String, String> keys;

    public void generateId() {
        if(this.subscriptionId == null){
            this.subscriptionId = UUID.randomUUID().toString();
        }
    }

}
