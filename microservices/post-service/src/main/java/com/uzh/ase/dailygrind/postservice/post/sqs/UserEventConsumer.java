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

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventConsumer {

    @Value("${dg.us.aws.sqs.queue-url}")
    private String queueUrl;

    private final SqsClient sqsClient;

    private final ObjectMapper objectMapper;

    private final UserService userService;

    @PostConstruct
    public void startPolling() {
        Thread pollingThread = new Thread(() -> {
            while (true) {
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageAttributeNames("All")
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(20)
                    .build();

                List<Message> messages = sqsClient.receiveMessage(request).messages();

                for (Message message : messages) {
                    handleMessage(message);
                    // delete after successful processing
                    sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build());
                }
            }
        });
        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    private void handleMessage(Message message) {
        String payload = message.body();
        Map<String, MessageAttributeValue> attributes = message.messageAttributes();

        String eventType = attributes.getOrDefault("eventType", MessageAttributeValue.builder().stringValue("UNKNOWN").build()).stringValue();

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

    private void handleUserCreated(String payload) {
        log.info("Received USER_CREATED event with payload: {}", payload);
        try {
            UserDataEvent event = objectMapper.readValue(payload, UserDataEvent.class);
            userService.addNewUser(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDataEvent", e);
        }
    }

    private void handleUserUpdated(String payload) {
        log.info("Received USER_UPDATED event with payload: {}", payload);
        try {
            UserDataEvent event = objectMapper.readValue(payload, UserDataEvent.class);
            userService.updateUser(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDataEvent", e);
        }
    }

    private void handleUserDeleted(String payload) {
        log.info("Received USER_DELETED event with payload: {}", payload);
        try {
            UserDataEvent event = objectMapper.readValue(payload, UserDataEvent.class);
            userService.deleteUser(event.userId());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDataEvent", e);
        }
    }

    private void handleFriendshipCreated(String payload) {
        log.info("Received FRIENDSHIP_CREATED event with payload: {}", payload);
        try {
            FriendshipEvent event = objectMapper.readValue(payload, FriendshipEvent.class);
            userService.addFriend(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDataEvent", e);
        }
    }

    private void handleFriendshipDeleted(String payload) {
        log.info("Received FRIENDSHIP_DELETED event with payload: {}", payload);
        try {
            FriendshipEvent event = objectMapper.readValue(payload, FriendshipEvent.class);
            userService.removeFriend(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse UserDataEvent", e);
        }
    }
}
