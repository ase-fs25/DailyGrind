package com.uzh.ase.dailygrind.userservice.user.controller.dto;

public record UserJobDto(
        String jobId,
        String jobTitle,
        String companyName,
        String startDate,
        String endDate,
        String location,
        String description
) {
}
