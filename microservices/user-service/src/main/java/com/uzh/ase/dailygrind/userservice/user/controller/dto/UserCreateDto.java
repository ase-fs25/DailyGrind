package com.uzh.ase.dailygrind.userservice.user.controller.dto;

public record UserCreateDto(
        String userId,
        String email,
        String firstName,
        String lastName,
        String birthday,
        String location,
        String profilePictureUrl
) {
}
