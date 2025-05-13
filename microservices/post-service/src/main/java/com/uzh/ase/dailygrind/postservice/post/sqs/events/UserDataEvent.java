package com.uzh.ase.dailygrind.postservice.post.sqs.events;

/**
 * Represents an event that carries user data, typically when a user's information is created or updated.
 * This event includes the user's ID, email, name, and profile picture URL.
 */
public record UserDataEvent(
    /**
     * The unique identifier of the user.
     */
    String userId,

    /**
     * The email address associated with the user.
     */
    String email,

    /**
     * The first name of the user.
     */
    String firstName,

    /**
     * The last name of the user.
     */
    String lastName,

    /**
     * The URL of the user's profile picture.
     */
    String profilePictureUrl
) {
}
