package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Map;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscription {


    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("subscriptionId")})
    private String subscriptionId;

    private String userId;

    private String endpoint;

    Long expirationTime;

    Map<String, String> keys;

}
