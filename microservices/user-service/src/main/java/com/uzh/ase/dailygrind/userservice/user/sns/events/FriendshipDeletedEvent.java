package com.uzh.ase.dailygrind.userservice.user.sns.events;

import java.time.LocalDateTime;

public record FriendshipDeletedEvent(
    String userAId,
    String userBId,
    LocalDateTime timestamp
) { }
