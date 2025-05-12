package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * A Data Transfer Object (DTO) representing a user.
 * <p>
 * This record contains basic user information such as the user's unique identifier, email,
 * full name, and profile picture URL.
 */
public record UserDto(
    /**
     * The unique identifier of the user.
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
