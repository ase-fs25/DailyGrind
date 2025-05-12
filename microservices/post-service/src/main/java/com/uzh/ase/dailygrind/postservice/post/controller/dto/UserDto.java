package com.uzh.ase.dailygrind.postservice.post.controller.dto;

public record UserDto(
    String userId,
    String email,
    String firstName,
    String lastName,
    String profilePictureUrl
) {
}
