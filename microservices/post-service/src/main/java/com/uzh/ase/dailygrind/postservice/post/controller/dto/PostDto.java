package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * A Data Transfer Object (DTO) representing a post, typically used to transfer
 * post data in API responses.
 * <p>
 * This record contains basic information about a post, including the title,
 * content, timestamp, and counts for likes and comments, as well as flags
 * indicating whether the post is liked by the current user and whether it is pinned.
 */
public record PostDto(
    /**
     * The unique identifier for the post.
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
     * A flag indicating whether the post is liked by the current user.
     */
    boolean isLiked,

    /**
     * A flag indicating whether the post is pinned.
     */
    boolean isPinned
) {
}
