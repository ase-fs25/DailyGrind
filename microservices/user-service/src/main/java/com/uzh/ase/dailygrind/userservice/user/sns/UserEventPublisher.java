package com.uzh.ase.dailygrind.userservice.user.sns;

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

    public void publishUserEvent(EventType eventType, UserDataEvent userDataEvent) {
        Map<String, MessageAttributeValue> messageAttributes = Map.of(
            "eventType", MessageAttributeValue.builder()
                .dataType("String")
                .stringValue(eventType.getEventType())
                .build()
        );

        snsClient.publish(publishRequest -> publishRequest
            .topicArn(topicArn)
            .message(userDataEvent.toString())
            .messageAttributes(messageAttributes)
        );
    }

}
