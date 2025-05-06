package com.uzh.ase.dailygrind.postservice.post.sqs.events;

import java.time.LocalDateTime;

public record FriendshipDeletedEvent(
    String userAId,
    String userBId,
    LocalDateTime timestamp
) { }
