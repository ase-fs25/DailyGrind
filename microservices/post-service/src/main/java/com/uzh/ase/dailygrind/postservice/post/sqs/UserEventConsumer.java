package com.uzh.ase.dailygrind.postservice.post.sqs;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserEventConsumer {

    @Value("${dg.us.aws.sqs.queue-url}")
    private String queueUrl;

    private final SqsClient sqsClient;

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
                System.out.println("Unknown eventType: " + eventType);
        }
    }

    // Define these methods accordingly
    private void handleUserCreated(String payload) {
        /* parse and create */
        System.out.println("User created: " + payload);
    }
    private void handleUserUpdated(String payload) { /* parse and update */ }
    private void handleUserDeleted(String payload) { /* parse and delete */ }
    private void handleFriendshipCreated(String payload) { /* logic */ }
    private void handleFriendshipDeleted(String payload) { /* logic */ }
}
