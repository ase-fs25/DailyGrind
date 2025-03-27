package com.uzh.ase.dailygrind.postservice.post.controller.dto;

public record PostDto(
        String id,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
