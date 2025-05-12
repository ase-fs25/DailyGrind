package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserCreateDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDetailsDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
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

/**
 * Controller for handling user-related operations.
 * <p>
 * This controller exposes endpoints for retrieving, creating, updating, and deleting user information.
 * </p>
 */
@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Fetches all users' information.
     *
     * @param principal the authenticated user's principal
     * @return a list of all users' information
     */
    @Operation(summary = "Get all users info", description = "Fetches all users info.")
    @ApiResponse(responseCode = "200", description = "List of users info retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping
    public List<UserInfoDto> getUsers(Principal principal) {
        return userService.getAllUserInfos(principal.getName());
    }

    /**
     * Fetches the details of the authenticated user.
     *
     * @param principal the authenticated user's principal
     * @return the details of the authenticated user
     */
    @Operation(summary = "Get current user's info", description = "Fetches the details of the authenticated info.")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class)))
    @GetMapping("/me")
    public UserInfoDto getMyInfo(Principal principal) {
        return userService.getUserInfoById(principal.getName(), principal.getName());
    }

    /**
     * Fetches the detailed information of the authenticated user.
     *
     * @param principal the authenticated user's principal
     * @return the detailed information of the authenticated user
     */
    @Operation(summary = "Get current user's details", description = "Fetches the details of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsDto.class)))
    @GetMapping("/me/details")
    public UserDetailsDto getMyDetails(Principal principal) {
        return userService.getUserDetailsById(principal.getName(), principal.getName());
    }

    /**
     * Fetches the detailed information of a user by their ID.
     *
     * @param userId the ID of the user whose details are to be fetched
     * @param principal the authenticated user's principal
     * @return the detailed information of the specified user
     */
    @Operation(summary = "Get user details by ID", description = "Fetches the details of a user by their ID.")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsDto.class)))
    @GetMapping("/{userId}/details")
    public UserDetailsDto getUserDetailsById(@PathVariable String userId, Principal principal) {
        return userService.getUserDetailsById(userId, principal.getName());
    }

    /**
     * Creates a new user with the provided details.
     *
     * @param createUserDto the user details to be created
     * @param principal the authenticated user's principal
     * @return the created user's information
     */
    @Operation(summary = "Create a new userInfo", description = "Creates a new userInfo with the provided details.")
    @ApiResponse(responseCode = "201", description = "User created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class)))
    @PostMapping("/me")
    public ResponseEntity<UserInfoDto> createUser(@RequestBody UserCreateDto createUserDto, Principal principal) {
        UserInfoDto createdUser = userService.createUser(createUserDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Updates the user information for the authenticated user.
     *
     * @param updateUserDto the user details to be updated
     * @param principal the authenticated user's principal
     * @return the updated user's information
     */
    @Operation(summary = "Update userInfo details", description = "Updates the user info of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "User info updated successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class)))
    @PutMapping("/me")
    public ResponseEntity<UserInfoDto> updateUser(@RequestBody UserCreateDto updateUserDto, Principal principal) {
        UserInfoDto updatedUser = userService.updateUser(updateUserDto, principal.getName());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes the authenticated user.
     *
     * @param principal the authenticated user's principal
     * @return a response indicating that the user was deleted
     */
    @Operation(summary = "Delete user", description = "Deletes the authenticated user.")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(Principal principal) {
        userService.deleteUser(principal.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for users whose first name starts with the given term.
     *
     * @param name the name term to search for
     * @param principal the authenticated user's principal
     * @return a list of users matching the search criteria
     */
    @Operation(summary = "Search users by name", description = "Search for users whose first name starts with the given term.")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/search")
    public ResponseEntity<List<UserInfoDto>> searchUsers(@RequestParam String name, Principal principal) {
        return ResponseEntity.ok(userService.searchUsersByName(name, principal.getName()));
    }
}
