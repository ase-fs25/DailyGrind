package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * A Data Transfer Object (DTO) representing a comment entry, including
 * both the comment details and the user who made the comment.
 * <p>
 * This record is used to transfer a comment and the associated user data
 * together, typically to provide a complete representation of a comment
 * and its author in API responses.
 */
public record CommentEntryDto(
    /**
     * The details of the comment.
     */
    CommentDto comment,

    /**
     * The user who made the comment.
     */
    UserDto user
) {
}
