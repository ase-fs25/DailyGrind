package com.uzh.ase.dailygrind.userservice.user.controller.dto;

import java.util.List;

public record UserDetailsDto(
        UserInfoDto userInfo,
        List<UserJobDto> jobs,
        List<UserEducationDto> educations
) {
}
