package com.uzh.ase.dailygrind.userservice.user.sns;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.userservice.user.sns.events.EventType;
import com.uzh.ase.dailygrind.userservice.user.sns.events.FriendshipEvent;
import com.uzh.ase.dailygrind.userservice.user.sns.events.UserDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // Import the logging annotation
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

import java.util.Map;

/**
 * Service responsible for publishing user-related events to SNS (Simple Notification Service).
 * <p>
 * This class handles the publishing of events such as user creation, update, deletion, and friendship changes
 * to an SNS topic for inter-service communication or notification.
 */
@Service
@RequiredArgsConstructor
@Slf4j  // Add this annotation for logging
public class UserEventPublisher {

    /**
     * The ARN (Amazon Resource Name) of the SNS topic to which events will be published.
     * This value is injected from the application properties file.
     */
    @Value("${dg.us.aws.sns.topic-arn}")
    private String topicArn;

    /**
     * The SNS client used to interact with the AWS SNS service.
     */
    private final SnsClient snsClient;

    /**
     * The object mapper used to serialize event data to JSON format.
     */
    private final ObjectMapper objectMapper;

    /**
     * Publishes a user-related event (e.g., user created, updated, or deleted) to the SNS topic.
     * <p>
     * The event is serialized to JSON, and the event type is included as a message attribute.
     *
     * @param eventType The type of the user event (e.g., USER_CREATED, USER_UPDATED, USER_DELETED).
     * @param userDataEvent The user data event to be published, containing the user's details.
     */
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
            log.info("Successfully published {} event for user ID: {}", eventType, userDataEvent.userId());
        } catch (Exception e) {
            log.error("Failed to publish {} event for user ID: {}", eventType, userDataEvent.userId(), e);
            throw new RuntimeException("Failed to convert UserDataEvent to JSON", e);
        }
    }

    /**
     * Publishes a friendship-related event (e.g., friendship created or deleted) to the SNS topic.
     * <p>
     * The event is serialized to JSON, and the event type is included as a message attribute.
     *
     * @param eventType The type of the friendship event (e.g., FRIENDSHIP_CREATED, FRIENDSHIP_DELETED).
     * @param friendshipEvent The friendship event to be published, containing the user IDs involved in the friendship.
     */
    public void publishFriendshipEvent(EventType eventType, FriendshipEvent friendshipEvent) {
        Map<String, MessageAttributeValue> messageAttributes = Map.of(
            "eventType", MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(eventType.getEventType())
                .build()
        );

        try {
            String json = objectMapper.writeValueAsString(friendshipEvent);
            snsClient.publish(publishRequest -> publishRequest
                .topicArn(topicArn)
                .message(json)
                .messageAttributes(messageAttributes)
            );
            log.info("Successfully published {} event for friendship between user ID: {} and user ID: {}", eventType, friendshipEvent.userAId(), friendshipEvent.userBId()); // Log success
        } catch (Exception e) {
            log.error("Failed to publish {} event for friendship between user ID: {} and user ID: {}", eventType, friendshipEvent.userAId(), friendshipEvent.userBId(), e); // Log error
            throw new RuntimeException("Failed to convert FriendshipEvent to JSON", e);
        }
    }
}
