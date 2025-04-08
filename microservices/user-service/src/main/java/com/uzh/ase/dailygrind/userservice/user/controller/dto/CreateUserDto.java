package com.uzh.ase.dailygrind.userservice.user.controller.dto;

public record CreateUserDto(
        String email,
        String firstName,
        String lastName,
        String location
) {
}
