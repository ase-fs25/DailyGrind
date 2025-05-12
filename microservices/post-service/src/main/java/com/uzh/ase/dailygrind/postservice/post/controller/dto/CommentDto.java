package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * A Data Transfer Object (DTO) representing a comment.
 * <p>
 * This record is used to transfer comment data between layers or over the network.
 * It encapsulates the basic details of a comment, such as the comment ID, user ID,
 * content, and the timestamp of when the comment was made.
 */
public record CommentDto(
    /**
     * The unique identifier for the comment.
     */
    String commentId,

    /**
     * The ID of the user who made the comment.
     */
    String userId,

    /**
     * The content of the comment.
     */
    String content,

    /**
     * The timestamp when the comment was created, typically in ISO-8601 format.
     */
    String timestamp
) {
}
