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

@RestController
@RequestMapping
@RequiredArgsConstructor
public class EducationController {

    private final UserEducationService userEducationService;

    @Operation(summary = "Get a userInfo's education details", description = "Fetches the list of education details associated with the specified userInfo.")
    @ApiResponse(responseCode = "200", description = "Education details retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/users/{userId}/education")
    public List<UserEducationDto> getUserEducation(@PathVariable String userId) {
        return userEducationService.getEducationForUser(userId);
    }

    @Operation(summary = "Get current userInfo's education details", description = "Fetches the list of education details associated with the authenticated userInfo.")
    @ApiResponse(responseCode = "200", description = "Education details retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/me/education")
    public List<UserEducationDto> getMyEducations(Principal principal) {
        return userEducationService.getEducationForUser(principal.getName());
    }

    @Operation(summary = "Create a new education for the current userInfo", description = "Creates a new education record for the authenticated userInfo.")
    @ApiResponse(responseCode = "201", description = "Education created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserEducationDto.class)))
    @PostMapping("/me/education")
    public ResponseEntity<UserEducationDto> createUserEducation(@RequestBody UserEducationDto createUserEducationDtos, Principal principal) {
        UserEducationDto createdUserEducation = userEducationService.createUserEducation(createUserEducationDtos, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserEducation);
    }

    @Operation(summary = "Update an education for the current userInfo", description = "Updates an existing education record for the authenticated userInfo.")
    @ApiResponse(responseCode = "200", description = "Education updated successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserEducationDto.class)))
    @PutMapping("/me/education/{educationId}")
    public ResponseEntity<UserEducationDto> updateUserEducation(@PathVariable String educationId, @RequestBody UserEducationDto updateUserEducationDto, Principal principal) {
        UserEducationDto updatedUserEducation = userEducationService.updateUserEducation(educationId, updateUserEducationDto, principal.getName());
        return ResponseEntity.ok(updatedUserEducation);
    }

    @Operation(summary = "Delete education for the current userInfo", description = "Deletes an education record from the authenticated userInfo's profile.")
    @ApiResponse(responseCode = "200", description = "Education record deleted successfully")
    @DeleteMapping("/me/education/{educationId}")
    public ResponseEntity<?> deleteUserEducation(@PathVariable String educationId, Principal principal) {
        userEducationService.deleteUserEducation(principal.getName(), educationId);
        return ResponseEntity.ok().build();
    }
}
