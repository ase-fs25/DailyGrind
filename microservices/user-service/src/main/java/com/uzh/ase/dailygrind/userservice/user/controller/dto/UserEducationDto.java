package com.uzh.ase.dailygrind.userservice.user.controller.dto;

public record UserEducationDto(
        String educationId,
        String institution,
        String degree,
        String fieldOfStudy,
        String startDate,
        String endDate,
        String location,
        String description
) {
}
