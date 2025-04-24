package com.uzh.ase.dailygrind.postservice.post.controller.dto;

public record CommentDto(
        String commentId,
        String content,
        String timestamp
) {
}
