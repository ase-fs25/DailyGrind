package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import java.util.Map;

@DynamoDBDocument
public record SubscriptionDto(
        String endpoint,
        Long expirationTime,
        Map<String, String> keys
) {
}