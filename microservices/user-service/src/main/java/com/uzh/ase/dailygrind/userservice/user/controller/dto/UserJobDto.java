package com.uzh.ase.dailygrind.userservice.user.controller.dto;

/**
 * Data Transfer Object (DTO) for user job details.
 * <p>
 * This DTO is used to transfer details about a user's job experience, including
 * job title, company name, employment dates, location, and job description.
 * </p>
 */
public record UserJobDto(
    /**
     * The unique identifier for the user's job.
     * This ID is used to reference the specific job entry.
     */
    String jobId,

    /**
     * The title of the user's job position.
     * This describes the role the user held at the company.
     */
    String jobTitle,

    /**
     * The name of the company where the user worked.
     */
    String companyName,

    /**
     * The start date of the job.
     * The date is represented as a string in the format YYYY-MM-DD.
     */
    String startDate,

    /**
     * The end date of the job.
     * The date is represented as a string in the format YYYY-MM-DD.
     * If the user is currently employed at the job, this may be null or empty.
     */
    String endDate,

    /**
     * The location of the job, typically the city or region where the job was based.
     */
    String location,

    /**
     * A description of the user's responsibilities and achievements in this job.
     * This field may provide details about the nature of the job and the user's contributions.
     */
    String description
) {}
