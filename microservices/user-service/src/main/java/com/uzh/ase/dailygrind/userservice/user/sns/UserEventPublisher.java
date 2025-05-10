package com.uzh.ase.dailygrind.userservice.user.sns;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.userservice.user.sns.events.EventType;
import com.uzh.ase.dailygrind.userservice.user.sns.events.UserDataEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    @Value("${dg.us.aws.sns.topic-arn}")
    private String topicArn;

    private final SnsClient snsClient;

    private final ObjectMapper objectMapper;

    public void publishUserEvent(EventType eventType, UserDataEvent userDataEvent) {
        Map<String, MessageAttributeValue> messageAttributes = Map.of(
            "eventType", MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(eventType.getEventType())
                .build()
        );

        try {
            String json = objectMapper.writeValueAsString(userDataEvent);
            snsClient.publish(publishRequest -> publishRequest
                .topicArn(topicArn)
                .message(json)
                .messageAttributes(messageAttributes)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert UserDataEvent to JSON", e);
        }
    }

}
