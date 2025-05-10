package com.uzh.ase.dailygrind.userservice.user.sns.events;

import java.time.LocalDateTime;

public record FriendshipCreatedEvent(
    String userAId,
    String userBId,
    LocalDateTime timestamp
) {
}
