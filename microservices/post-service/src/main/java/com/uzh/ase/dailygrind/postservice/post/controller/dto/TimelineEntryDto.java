package com.uzh.ase.dailygrind.postservice.post.controller.dto;

/**
 * A Data Transfer Object (DTO) representing a timeline entry.
 * <p>
 * This record combines a {@link PostDto} with a {@link UserDto}, allowing the
 * transfer of a post along with the user who created it in the context of a timeline.
 */
public record TimelineEntryDto(
    /**
     * The post included in the timeline entry.
     */
    PostDto post,

    /**
     * The user who created the post.
     */
    UserDto user
) {
}
