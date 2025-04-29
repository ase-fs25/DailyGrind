package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository.entity;

import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

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

    private SubscriptionDto subscription;

}
