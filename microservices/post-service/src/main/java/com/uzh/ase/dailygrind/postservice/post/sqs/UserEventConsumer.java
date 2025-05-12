package com.uzh.ase.dailygrind.postservice.post.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.postservice.post.service.UserService;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.FriendshipEvent;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.UserDataEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.Map;

/**
 * Service responsible for consuming user-related events from an SQS queue and processing them.
 * The events can include user creation, update, deletion, and friendship events.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventConsumer {

    /**
     * The URL of the SQS queue from which events will be consumed.
     * This value is injected from the application properties.
     */
    @Value("${dg.us.aws.sqs.queue-url}")
    private String queueUrl;

    private final SqsClient sqsClient;

    private final ObjectMapper objectMapper;

    private final UserService userService;

    /**
     * Initializes a polling thread that continuously checks for messages in the SQS queue.
     * The thread processes the messages and deletes them after successful handling.
     */
    @PostConstruct
    public void startPolling() {
        // Create a new thread to continuously poll the SQS queue
        Thread pollingThread = new Thread(() -> {
            while (true) {
                // Define the request for receiving messages from the SQS queue
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageAttributeNames("All")
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(20)
                    .build();

                // Receive messages from the queue
                List<Message> messages = sqsClient.receiveMessage(request).messages();

                // Process each message
                for (Message message : messages) {
                    handleMessage(message);
                    // Delete the message after successful processing
                    sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build());
                }
            }
        });
        // Set the thread to daemon mode so it doesn't block application shutdown
        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    /**
     * Handles incoming messages from the SQS queue based on the event type specified in the message attributes.
     *
     * @param message The SQS message to be processed.
     */
    private void handleMessage(Message message) {
        String payload = message.body();
        Map<String, MessageAttributeValue> attributes = message.messageAttributes();

        // Get the event type from the message attributes (defaults to "UNKNOWN" if not found)
        String eventType = attributes.getOrDefault("eventType", MessageAttributeValue.builder().stringValue("UNKNOWN").build()).stringValue();

        // Handle different event types based on the eventType value
        switch (eventType) {
            case "USER_CREATED":
                handleUserCreated(payload);
                break;
            case "USER_UPDATED":
                handleUserUpdated(payload);
                break;
            case "USER_DELETED":
                handleUserDeleted(payload);
                break;
            case "FRIENDSHIP_CREATED":
                handleFriendshipCreated(payload);
                break;
            case "FRIENDSHIP_DELETED":
                handleFriendshipDeleted(payload);
                break;
            default:
                log.error("Unknown event type: {}", eventType);
        }
    }

    /**
     * Handles the USER_CREATED event and triggers the user creation logic.
     *
     * @param payload The message payload containing the user data.
     */
    private void handleUserCreated(String payload) {
        log.info("Received USER_CREATED event with payload: {}", payload);
        try {
            // Convert the payload to a UserDataEvent object
            UserDataEvent event = objectMapper.readValue(payload, UserDataEvent.class);
            // Add the new user using the UserService
            userService.addNewUser(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDataEvent", e);
        }
    }

    /**
     * Handles the USER_UPDATED event and triggers the user update logic.
     *
     * @param payload The message payload containing the updated user data.
     */
    private void handleUserUpdated(String payload) {
        log.info("Received USER_UPDATED event with payload: {}", payload);
        try {
            // Convert the payload to a UserDataEvent object
            UserDataEvent event = objectMapper.readValue(payload, UserDataEvent.class);
            // Update the user data using the UserService
            userService.updateUser(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDataEvent", e);
        }
    }

    /**
     * Handles the USER_DELETED event and triggers the user deletion logic.
     *
     * @param payload The message payload containing the user ID of the deleted user.
     */
    private void handleUserDeleted(String payload) {
        log.info("Received USER_DELETED event with payload: {}", payload);
        try {
            // Convert the payload to a UserDataEvent object
            UserDataEvent event = objectMapper.readValue(payload, UserDataEvent.class);
            // Delete the user using the UserService
            userService.deleteUser(event.userId());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDataEvent", e);
        }
    }

    /**
     * Handles the FRIENDSHIP_CREATED event and triggers the logic for adding a friend.
     *
     * @param payload The message payload containing the friendship event data.
     */
    private void handleFriendshipCreated(String payload) {
        log.info("Received FRIENDSHIP_CREATED event with payload: {}", payload);
        try {
            // Convert the payload to a FriendshipEvent object
            FriendshipEvent event = objectMapper.readValue(payload, FriendshipEvent.class);
            // Add the new friendship using the UserService
            userService.addFriend(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse FriendshipEvent", e);
        }
    }

    /**
     * Handles the FRIENDSHIP_DELETED event and triggers the logic for removing a friend.
     *
     * @param payload The message payload containing the friendship event data.
     */
    private void handleFriendshipDeleted(String payload) {
        log.info("Received FRIENDSHIP_DELETED event with payload: {}", payload);
        try {
            // Convert the payload to a FriendshipEvent object
            FriendshipEvent event = objectMapper.readValue(payload, FriendshipEvent.class);
            // Remove the friendship using the UserService
            userService.removeFriend(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse FriendshipEvent", e);
        }
    }
}
