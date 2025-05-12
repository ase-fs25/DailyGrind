package com.uzh.ase.dailygrind.userservice.user.controller.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) for user details.
 * <p>
 * This DTO is used to transfer comprehensive details about a user, including
 * their basic information, associated jobs, and educational background.
 * </p>
 */
public record UserDetailsDto(
    /**
     * Basic information of the user.
     * This includes the user's personal details such as name, email, and location.
     */
    UserInfoDto userInfo,

    /**
     * A list of jobs associated with the user.
     * Each job includes details such as job title, company, and duration.
     * The list can be empty if the user has no job information.
     */
    List<UserJobDto> jobs,

    /**
     * A list of educations associated with the user.
     * Each education includes details such as degree, institution, and duration.
     * The list can be empty if the user has no education information.
     */
    List<UserEducationDto> educations
) {
}
