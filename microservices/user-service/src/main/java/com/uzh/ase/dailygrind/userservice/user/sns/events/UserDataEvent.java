package com.uzh.ase.dailygrind.userservice.user.sns.events;

/**
 * Represents a user data event that is published when a user's profile is created, updated, or deleted.
 * <p>
 * This event is used to synchronize user data across services.
 *
 * @param userId             The unique identifier of the user.
 * @param email              The email address of the user.
 * @param firstName          The user's first name.
 * @param lastName           The user's last name.
 * @param profilePictureUrl  The URL to the user's profile picture.
 */
public record UserDataEvent(
    String userId,
    String email,
    String firstName,
    String lastName,
    String profilePictureUrl
) {
}
