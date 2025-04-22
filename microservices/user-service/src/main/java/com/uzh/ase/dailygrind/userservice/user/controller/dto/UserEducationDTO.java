package com.uzh.ase.dailygrind.userservice.user.controller.dto;

public record UserEducationDTO(
        String educationId,
        String schoolName,
        String degree,
        String fieldOfStudy,
        String startDate,
        String endDate,
        String location,
        String description
) {
}
