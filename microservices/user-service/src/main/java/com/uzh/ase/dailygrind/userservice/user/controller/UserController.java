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

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users info", description = "Fetches all users info.")
    @ApiResponse(responseCode = "200", description = "List of users info retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @GetMapping("/users")
    public List<UserInfoDto> getUsers(Principal principal) {
        return userService.getAllUserInfos(principal.getName());
    }

    @Operation(summary = "Get current user's details", description = "Fetches the details of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateDto.class)))
    @GetMapping("/users/me")
    public UserDetailsDto getMyDetails(Principal principal) {
        return userService.getUserDetailsById(principal.getName(), principal.getName());
    }

    @Operation(summary = "Get user details by ID", description = "Fetches the details of a user by their ID.")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateDto.class)))
    @GetMapping("/users/{userId}")
    public UserDetailsDto getUserDetailsById(@PathVariable String userId, Principal principal) {
        return userService.getUserDetailsById(userId, principal.getName());
    }

    @Operation(summary = "Create a new userInfo", description = "Creates a new userInfo with the provided details.")
    @ApiResponse(responseCode = "201", description = "User created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateDto.class)))
    @PostMapping("/users/me")
    public ResponseEntity<UserInfoDto> createUser(@RequestBody UserCreateDto createUserDto, Principal principal) {
        UserInfoDto createdUser = userService.createUser(createUserDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Update userInfo details", description = "Updates the user info of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "User info updated successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateDto.class)))
    @PutMapping("/users/me")
    public ResponseEntity<UserInfoDto> updateUser(@RequestBody UserCreateDto updateUserDto, Principal principal) {
        UserInfoDto updatedUser = userService.updateUser(updateUserDto, principal.getName());
        return ResponseEntity.ok(updatedUser);
    }
}
