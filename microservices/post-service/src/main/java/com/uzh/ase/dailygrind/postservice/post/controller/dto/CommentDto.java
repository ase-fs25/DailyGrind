package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * Data Transfer Object (DTO) representing a comment.
 * <p>
 * This record is used to transfer comment data between the layers of the application.
 * It encapsulates the comment's ID, the user who made the comment, the content of the comment,
 * and the timestamp of when the comment was created.
 */
public record CommentDto(
    /**
     * The unique identifier of the comment.
     */
    String commentId,

    /**
     * The ID of the user who created the comment.
     */
    String userId,

    /**
     * The content of the comment.
     */
    String content,

    /**
     * The timestamp when the comment was created.
     */
    String timestamp
) {
}
