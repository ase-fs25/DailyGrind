package com.uzh.ase.dailygrind.postservice.post.sqs.events;

public record UserDataEvent(
    String userId,
    String email,
    String firstName,
    String lastName,
    String profilePictureUrl
) {
}
