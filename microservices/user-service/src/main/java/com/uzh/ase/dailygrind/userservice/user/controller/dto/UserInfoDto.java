package com.uzh.ase.dailygrind.userservice.user.controller.dto;

public record UserInfoDto(
        String userId,
        String email,
        String firstName,
        String lastName,
        String birthday,
        String location,
        int numFollowers,
        int numFollowing,
        boolean isFollowing
) {
}
