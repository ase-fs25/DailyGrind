package com.uzh.ase.dailygrind.userservice.user.sns.events;

public record FriendshipEvent(
    String userAId,
    String userBId
) {
}
