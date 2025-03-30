package com.uzh.ase.dailygrind.mspushnotifications.pushNotification.controller.dto;

import java.util.Map;

public record SubscriptionDto(
        String endpoint,
        Long expirationTime,
        Map<String, String> keys
) {
}