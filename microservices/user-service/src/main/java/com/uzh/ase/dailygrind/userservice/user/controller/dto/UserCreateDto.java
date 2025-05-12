package com.uzh.ase.dailygrind.userservice.user.controller.dto;

/**
 * Data Transfer Object (DTO) for creating a new user.
 * <p>
 * This DTO is used to transfer the necessary information for creating a new user
 * in the system. It includes basic user information such as user ID, email, name,
 * birthday, location, and profile picture URL.
 * </p>
 */
public record UserCreateDto(
    /**
     * The unique identifier for the user.
     * This value should be provided when creating a new user.
     */
    String userId,

    /**
     * The email address of the user.
     * This value should be valid and unique within the system.
     */
    String email,

    /**
     * The first name of the user.
     * This is a required field.
     */
    String firstName,

    /**
     * The last name of the user.
     * This is a required field.
     */
    String lastName,

    /**
     * The birthday of the user.
     * The date should be provided in a string format (e.g., "YYYY-MM-DD").
     */
    String birthday,

    /**
     * The location of the user.
     * This value can represent a city, country, or other geographical location.
     */
    String location,

    /**
     * The URL of the user's profile picture.
     * This is an optional field.
     */
    String profilePictureUrl
) {
}
