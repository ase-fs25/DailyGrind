package com.uzh.ase.dailygrind.userservice.user.controller.dto;

public record UserDto(
        String id,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
