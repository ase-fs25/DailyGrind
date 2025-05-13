package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * Data Transfer Object (DTO) representing a timeline entry, which includes a post and
 * the user associated with it.
 * <p>
 * This record is used to transfer data for displaying posts along with the associated user
 * information in a timeline view.
 */
public record TimelineEntryDto(
    /**
     * The post associated with this timeline entry.
     */
    PostDto post,

    /**
     * The user who created or is associated with this post.
     */
    UserDto user
) {
}
