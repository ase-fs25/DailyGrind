package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
import com.uzh.ase.dailygrind.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ───── USER INFO ─────────────────────────────

    @Operation(summary = "Get user details by ID", description = "Fetches the details of a user by their ID.")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/users/{userId}")
    public UserDto getUserDetailsById(@PathVariable String userId) {
        return userService.getUserDetailsById(userId);
    }

    @Operation(summary = "Get current user's details", description = "Fetches the details of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/me")
    public UserDto getMyDetails(Principal principal) {
        return userService.getUserDetailsById(principal.getName());
    }

    @Operation(summary = "Get all users' details", description = "Fetches the details of all users.")
    @ApiResponse(responseCode = "200", description = "List of user details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/users")
    public List<UserDto> getUsersDetails() {
        return userService.getAllUserDetails();
    }

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details.")
    @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto createUserDto, Principal principal) {
        UserDto createdUser = userService.createUser(createUserDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // ───── FOLLOW SYSTEM ─────────────────────────

    @Operation(summary = "Check if the current user is following another user", description = "Checks if the authenticated user is following the user with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Following status retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @GetMapping("/me/followers/{userId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) return ResponseEntity.ok(false);
        return ResponseEntity.ok(userService.isFollowing(userId, principal.getName()));
    }

    @Operation(summary = "Check if the current user is followed by another user", description = "Checks if the authenticated user is followed by the user with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Followed status retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @GetMapping("/me/following/{userId}")
    public ResponseEntity<Boolean> isFollowed(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) return ResponseEntity.ok(false);
        return ResponseEntity.ok(userService.isFollowing(principal.getName(), userId));
    }

    @Operation(summary = "Follow a user", description = "Allows the authenticated user to follow the specified user.")
    @ApiResponse(responseCode = "200", description = "User followed successfully")
    @ApiResponse(responseCode = "400", description = "Cannot follow yourself", content = @Content(mediaType = "application/json"))
    @PutMapping("/me/following/{userId}")
    public ResponseEntity<?> followUser(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) {
            return ResponseEntity.badRequest().body("You cannot follow yourself.");
        }
        userService.followUser(userId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unfollow a user", description = "Allows the authenticated user to unfollow the specified user.")
    @ApiResponse(responseCode = "200", description = "User unfollowed successfully")
    @ApiResponse(responseCode = "400", description = "Cannot unfollow yourself", content = @Content(mediaType = "application/json"))
    @DeleteMapping("/me/following/{userId}")
    public ResponseEntity<?> unfollowUser(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) {
            return ResponseEntity.badRequest().body("You cannot unfollow yourself.");
        }
        userService.unfollowUser(userId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get followers of a user", description = "Fetches the list of users who follow the specified user.")
    @ApiResponse(responseCode = "200", description = "Followers list retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/users/{userId}/followers")
    public List<UserDto> getFollowers(@PathVariable String userId) {
        return userService.getFollowers(userId);
    }

    @Operation(summary = "Get following users of a user", description = "Fetches the list of users whom the specified user is following.")
    @ApiResponse(responseCode = "200", description = "Following list retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/users/{userId}/following")
    public List<UserDto> getFollowing(@PathVariable String userId) {
        return userService.getFollowing(userId);
    }

    @Operation(summary = "Get current user's followers", description = "Fetches the list of followers of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Followers list retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/me/followers")
    public List<UserDto> getMyFollowers(Principal principal) {
        return userService.getFollowers(principal.getName());
    }

    @Operation(summary = "Get current user's following list", description = "Fetches the list of users whom the authenticated user is following.")
    @ApiResponse(responseCode = "200", description = "Following list retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/me/following")
    public List<UserDto> getMyFollowing(Principal principal) {
        return userService.getFollowing(principal.getName());
    }

    // ───── JOBS ──────────────────────────────────

    @Operation(summary = "Get a user's jobs", description = "Fetches the list of jobs associated with the specified user.")
    @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/users/{userId}/jobs")
    public List<UserJobDto> getUserJobs(@PathVariable String userId) {
        return userService.getJobsForUser(userId);
    }

    @Operation(summary = "Get current user's jobs", description = "Fetches the list of jobs associated with the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/me/jobs")
    public List<UserJobDto> getMyJobs(Principal principal) {
        return userService.getJobsForUser(principal.getName());
    }

    @Operation(summary = "Delete a job for the current user", description = "Deletes a job from the authenticated user's profile.")
    @ApiResponse(responseCode = "200", description = "Job deleted successfully")
    @DeleteMapping("/me/jobs/{jobId}")
    public ResponseEntity<?> deleteUserJob(@PathVariable String jobId, Principal principal) {
        userService.deleteUserJob(principal.getName(), jobId);
        return ResponseEntity.ok().build();
    }

    // ───── EDUCATION ─────────────────────────────

    @Operation(summary = "Get a user's education details", description = "Fetches the list of education details associated with the specified user.")
    @ApiResponse(responseCode = "200", description = "Education details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/users/{userId}/education")
    public List<UserEducationDto> getUserEducation(@PathVariable String userId) {
        return userService.getEducationForUser(userId);
    }

    @Operation(summary = "Get current user's education details", description = "Fetches the list of education details associated with the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Education details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/me/education")
    public List<UserEducationDto> getMyEducations(Principal principal) {
        return userService.getEducationForUser(principal.getName());
    }

    @Operation(summary = "Delete education for the current user", description = "Deletes an education record from the authenticated user's profile.")
    @ApiResponse(responseCode = "200", description = "Education record deleted successfully")
    @DeleteMapping("/me/education/{educationId}")
    public ResponseEntity<?> deleteUserEducation(@PathVariable String educationId, Principal principal) {
        userService.deleteUserEducation(principal.getName(), educationId);
        return ResponseEntity.ok().build();
    }
}