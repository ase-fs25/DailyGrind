package com.uzh.ase.dailygrind.postservice.post.sqs.events;

public record FriendshipEvent(
    String userAId,
    String userBId
) {
}
