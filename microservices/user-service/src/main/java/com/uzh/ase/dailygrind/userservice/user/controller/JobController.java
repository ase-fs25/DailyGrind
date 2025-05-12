package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
import com.uzh.ase.dailygrind.userservice.user.service.UserJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controller for handling job-related operations.
 * <p>
 * This controller exposes endpoints for retrieving, creating, updating, and deleting jobs associated with users.
 * </p>
 */
@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class JobController {

    private final UserJobService userJobService;

    /**
     * Fetches the list of jobs associated with the specified user.
     *
     * @param userId the ID of the user whose jobs are to be fetched
     * @return a list of jobs associated with the specified user
     */
    @Operation(summary = "Get a user's jobs", description = "Fetches the list of jobs associated with the specified user.")
    @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserJobDto[].class)))
    @GetMapping("/{userId}/jobs")
    public List<UserJobDto> getUserJobs(@PathVariable String userId) {
        return userJobService.getJobsForUser(userId);
    }

    /**
     * Fetches the list of jobs associated with the authenticated user.
     *
     * @param principal the authenticated user's principal
     * @return a list of jobs associated with the authenticated user
     */
    @Operation(summary = "Get current user's jobs", description = "Fetches the list of jobs associated with the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserJobDto[].class)))
    @GetMapping("/me/jobs")
    public List<UserJobDto> getMyJobs(Principal principal) {
        return userJobService.getJobsForUser(principal.getName());
    }

    /**
     * Creates a new job for the authenticated user.
     *
     * @param createUserJobDto the job details to be created
     * @param principal the authenticated user's principal
     * @return the created job information
     */
    @Operation(summary = "Create a new job for the current user", description = "Creates a new job for the authenticated user.")
    @ApiResponse(responseCode = "201", description = "Job created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserJobDto.class)))
    @PostMapping("/me/jobs")
    public ResponseEntity<UserJobDto> createUserJob(@RequestBody UserJobDto createUserJobDto, Principal principal) {
        UserJobDto createdUserJob = userJobService.createUserJob(createUserJobDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserJob);
    }

    /**
     * Updates an existing job for the authenticated user.
     *
     * @param jobId the ID of the job to be updated
     * @param updateUserJobDto the updated job details
     * @param principal the authenticated user's principal
     * @return the updated job information
     */
    @Operation(summary = "Update a job for the current user", description = "Updates an existing job for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Job updated successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserJobDto.class)))
    @PutMapping("/me/jobs/{jobId}")
    public ResponseEntity<UserJobDto> updateUserJob(@PathVariable String jobId, @RequestBody UserJobDto updateUserJobDto, Principal principal) {
        UserJobDto updatedUserJob = userJobService.updateUserJob(jobId, updateUserJobDto, principal.getName());
        return ResponseEntity.ok(updatedUserJob);
    }

    /**
     * Deletes a job from the authenticated user's profile.
     *
     * @param jobId the ID of the job to be deleted
     * @param principal the authenticated user's principal
     * @return a response indicating that the job was deleted
     */
    @Operation(summary = "Delete a job for the current user", description = "Deletes a job from the authenticated user's profile.")
    @ApiResponse(responseCode = "200", description = "Job deleted successfully")
    @DeleteMapping("/me/jobs/{jobId}")
    public ResponseEntity<Void> deleteUserJob(@PathVariable String jobId, Principal principal) {
        userJobService.deleteUserJob(jobId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
