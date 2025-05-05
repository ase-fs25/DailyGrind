package com.uzh.ase.dailygrind.userservice.user.sns.events;

public record UserDataEvent(
    String userId,
    String email,
    String firstName,
    String lastName,
    String profilePictureUrl
) {
}
