package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * Data Transfer Object (DTO) representing a comment entry, which includes both the comment
 * and the user who made the comment.
 * <p>
 * This record is used to transfer data related to a comment and the user who created it.
 * It encapsulates a {@link CommentDto} and a {@link UserDto}.
 */
public record CommentEntryDto(
    /**
     * The comment details encapsulated in a {@link CommentDto}.
     */
    CommentDto comment,

    /**
     * The user details of the user who created the comment, encapsulated in a {@link UserDto}.
     */
    UserDto user
) {
}
