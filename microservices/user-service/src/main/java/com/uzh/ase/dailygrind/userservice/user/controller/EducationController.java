package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import com.uzh.ase.dailygrind.userservice.user.service.UserEducationService;
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
 * Controller for handling user education-related operations.
 * <p>
 * This controller exposes endpoints for retrieving, creating, updating, and deleting education records
 * for users. The operations are based on the authenticated user's context or a specified user ID.
 * </p>
 */
@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class EducationController {

    private final UserEducationService userEducationService;

    /**
     * Fetches the list of education details associated with the specified user.
     *
     * @param userId the ID of the user whose education details are to be retrieved
     * @return a list of education details
     */
    @Operation(summary = "Get a user's education details", description = "Fetches the list of education details associated with the specified user.")
    @ApiResponse(responseCode = "200", description = "Education details retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserEducationDto[].class)))
    @GetMapping("/{userId}/education")
    public List<UserEducationDto> getUserEducation(@PathVariable String userId) {
        return userEducationService.getEducationForUser(userId);
    }

    /**
     * Fetches the list of education details associated with the authenticated user.
     *
     * @param principal the authenticated user's principal containing the user ID
     * @return a list of education details for the authenticated user
     */
    @Operation(summary = "Get current user's education details", description = "Fetches the list of education details associated with the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Education details retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserEducationDto[].class)))
    @GetMapping("/me/education")
    public List<UserEducationDto> getMyEducations(Principal principal) {
        return userEducationService.getEducationForUser(principal.getName());
    }

    /**
     * Creates a new education record for the authenticated user.
     *
     * @param createUserEducationDtos the education details to be created
     * @param principal the authenticated user's principal
     * @return the created education record
     */
    @Operation(summary = "Create a new education for the current user", description = "Creates a new education record for the authenticated user.")
    @ApiResponse(responseCode = "201", description = "Education created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserEducationDto.class)))
    @PostMapping("/me/education")
    public ResponseEntity<UserEducationDto> createUserEducation(@RequestBody UserEducationDto createUserEducationDtos, Principal principal) {
        UserEducationDto createdUserEducation = userEducationService.createUserEducation(createUserEducationDtos, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserEducation);
    }

    /**
     * Updates an existing education record for the authenticated user.
     *
     * @param educationId the ID of the education record to be updated
     * @param updateUserEducationDto the new education details
     * @param principal the authenticated user's principal
     * @return the updated education record
     */
    @Operation(summary = "Update an education for the current user", description = "Updates an existing education record for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Education updated successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserEducationDto.class)))
    @PutMapping("/me/education/{educationId}")
    public ResponseEntity<UserEducationDto> updateUserEducation(@PathVariable String educationId, @RequestBody UserEducationDto updateUserEducationDto, Principal principal) {
        UserEducationDto updatedUserEducation = userEducationService.updateUserEducation(educationId, updateUserEducationDto, principal.getName());
        return ResponseEntity.ok(updatedUserEducation);
    }

    /**
     * Deletes an education record from the authenticated user's profile.
     *
     * @param educationId the ID of the education record to be deleted
     * @param principal the authenticated user's principal
     * @return a response indicating that the education record has been deleted
     */
    @Operation(summary = "Delete education for the current user", description = "Deletes an education record from the authenticated user's profile.")
    @ApiResponse(responseCode = "200", description = "Education record deleted successfully")
    @DeleteMapping("/me/education/{educationId}")
    public ResponseEntity<Void> deleteUserEducation(@PathVariable String educationId, Principal principal) {
        userEducationService.deleteUserEducation(principal.getName(), educationId);
        return ResponseEntity.noContent().build();
    }
}
