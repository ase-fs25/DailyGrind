package com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uzh.ase.dailygrind.pushnotificationsservice.pushNotification.controller.dto.SubscriptionDto;

import java.nio.charset.StandardCharsets;

public class SubscriptionDtoConverter implements DynamoDBTypeConverter<String, SubscriptionDto> {
    private final ObjectMapper objectMapper;

    public SubscriptionDtoConverter() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String convert(SubscriptionDto subscription) {
        try {
            if (subscription == null) {
                return null;
            }
            return objectMapper.writeValueAsString(subscription);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting SubscriptionDto to JSON", e);
        }
    }

    @Override
    public SubscriptionDto unconvert(String json) {
        try {
            if (json == null || json.isEmpty()) {
                System.out.println("Warning: Empty JSON string received from DynamoDB");
                return null;
            }

            System.out.println("Raw input: " + json);

            // prepare data
            if (json.contains("\"bytes\"") && json.contains("\"type\":\"Buffer\"")) {
                try {
                    JsonNode node = objectMapper.readTree(json);

                    byte[] bytes = objectMapper.convertValue(node.path("bytes").path("data"), byte[].class);

                    json = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("Extracted JSON from bytes: " + json);
                } catch (Exception e) {
                    System.err.println("Failed to extract JSON from binary format: " + e.getMessage());
                }
            }

            // parse the subscription JSON
            SubscriptionDto result = objectMapper.readValue(json, SubscriptionDto.class);
            System.out.println("Successfully parsed SubscriptionDto with endpoint: " +
                    (result.endpoint() != null ? result.endpoint() : "null"));

            return result;

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON: " + json);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error converting JSON to SubscriptionDto", e);
        }
    }
}