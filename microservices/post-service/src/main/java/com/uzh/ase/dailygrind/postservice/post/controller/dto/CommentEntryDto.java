package com.uzh.ase.dailygrind.postservice.post.controller.dto;

public record CommentEntryDto(
    CommentDto comment,
    UserDto user
) {
}
