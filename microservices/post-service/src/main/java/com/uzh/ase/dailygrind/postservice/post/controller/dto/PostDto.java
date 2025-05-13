package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * Data Transfer Object (DTO) representing a post, including details such as title, content,
 * like count, comment count, and whether the post is liked or pinned by the user.
 * <p>
 * This record is used to transfer data related to a post.
 */
public record PostDto(
    /**
     * The unique identifier of the post.
     */
    String postId,

    /**
     * The title of the post.
     */
    String title,

    /**
     * The content or body of the post.
     */
    String content,

    /**
     * The timestamp when the post was created.
     */
    String timestamp,

    /**
     * The number of likes the post has received.
     */
    Long likeCount,

    /**
     * The number of comments the post has received.
     */
    Long commentCount,

    /**
     * Indicates whether the post is liked by the current user.
     */
    boolean isLiked,

    /**
     * Indicates whether the post is pinned by the current user.
     */
    boolean isPinned
) {
}
