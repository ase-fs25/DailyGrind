package com.uzh.ase.dailygrind.postservice.post.controller.dto;

public record TimelineEntryDto(
    PostDto post,
    UserDto user
) {
}
