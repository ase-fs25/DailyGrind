package com.uzh.ase.dailygrind.postservice.post.controller.dto;

public record PostDto(
        String postId,
        String title,
        String content,
        String timestamp,
        Long likeCount,
        Long commentCount,
        boolean isLiked,
        boolean isPinned
) {
}
