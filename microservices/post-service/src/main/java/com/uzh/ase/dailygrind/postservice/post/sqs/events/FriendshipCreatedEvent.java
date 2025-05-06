package com.uzh.ase.dailygrind.postservice.post.sqs.events;

import java.time.LocalDateTime;

public record FriendshipCreatedEvent(
    String userAId,
    String userBId,
    LocalDateTime timestamp
) {
}
