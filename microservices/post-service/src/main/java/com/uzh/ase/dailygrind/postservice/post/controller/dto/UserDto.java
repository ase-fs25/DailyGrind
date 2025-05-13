package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * Data Transfer Object (DTO) representing a user, containing the user's basic details
 * such as their ID, email, name, and profile picture.
 * <p>
 * This record is used to transfer user-related data, typically for displaying user details
 * in various contexts such as posts, comments, and profile views.
 */
public record UserDto(
    /**
     * The unique identifier for the user.
     */
    String userId,

    /**
     * The email address of the user.
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
