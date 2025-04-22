package com.uzh.ase.dailygrind.userservice.user.controller.dto;

import java.util.List;

public record UserDto(
        String userId,
        String email,
        String firstName,
        String lastName,
        String birthday,
        String location,
        List<UserJobDTO> jobs,
        List<UserEducationDTO> education
) {
}
