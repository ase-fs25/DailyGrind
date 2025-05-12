package com.uzh.ase.dailygrind.userservice.user.controller.dto;

/**
 * Data Transfer Object (DTO) for basic user information.
 * <p>
 * This DTO is used to transfer basic user details such as the user's ID, email,
 * name, birthday, location, profile picture, and the number of friends. It also
 * includes a flag indicating whether the user is a friend.
 * </p>
 */
public record UserInfoDto(
    /**
     * The unique identifier for the user.
     * This ID is used to reference the user in the system.
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
     * The user's date of birth.
     * The date is represented as a string in the format YYYY-MM-DD.
     */
    String birthday,

    /**
     * The user's location (e.g., city or region).
     */
    String location,

    /**
     * The number of friends the user has.
     * This value represents the total count of the user's friends.
     */
    int numberOfFriends,

    /**
     * The URL to the user's profile picture.
     * This is typically used to display the user's avatar in the UI.
     */
    String profilePictureUrl,

    /**
     * A boolean indicating whether the user is a friend of the current user.
     * This is used to indicate friendship status in the system.
     */
    boolean isFriend
) {}
