package com.uzh.ase.dailygrind.userservice.user.controller.dto;

/**
 * Data Transfer Object (DTO) for user education details.
 * <p>
 * This DTO is used to transfer information about a user's education, including
 * the institution, degree, field of study, and other relevant details.
 * </p>
 */
public record UserEducationDto(
    /**
     * The unique identifier for the education entry.
     * This ID is used to reference a specific education record.
     */
    String educationId,

    /**
     * The name of the institution where the user received their education.
     */
    String institution,

    /**
     * The degree obtained by the user (e.g., Bachelor's, Master's, PhD).
     */
    String degree,

    /**
     * The field of study for the user's education (e.g., Computer Science, Physics).
     */
    String fieldOfStudy,

    /**
     * The start date of the user's education at the institution.
     * The date is represented as a string in the format YYYY-MM-DD.
     */
    String startDate,

    /**
     * The end date of the user's education at the institution.
     * The date is represented as a string in the format YYYY-MM-DD.
     */
    String endDate,

    /**
     * The location of the institution where the education took place.
     */
    String location,

    /**
     * A description of the user's education experience, such as key achievements or focus areas.
     */
    String description
) {
}
