package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.service.UserFollowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class FollowerController {

    private final UserFollowerService userFollowerService;

    @Operation(summary = "Check if the current user is following another user", description = "Checks if the authenticated user is following the user with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Following status retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @GetMapping("/{userId}/is-followed")
    public ResponseEntity<Boolean> isFollowing(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) return ResponseEntity.ok(false);
        return ResponseEntity.ok(userFollowerService.isFollowing(principal.getName(), userId));
    }

    @Operation(summary = "Check if the current user is followed by another user", description = "Checks if the authenticated user is followed by the user with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Followed status retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @GetMapping("/{userId}/is-follower")
    public ResponseEntity<Boolean> isFollowed(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) return ResponseEntity.ok(false);
        return ResponseEntity.ok(userFollowerService.isFollowing(userId, principal.getName()));
    }

    // --- Current userInfo followers/following lists ---
    @Operation(summary = "Get current user's followers", description = "Fetches the list of followers of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Followers list retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/me/followers")
    public List<UserInfoDto> getMyFollowers(Principal principal) {
        return userFollowerService.getFollowers(principal.getName());
    }

    @Operation(summary = "Get current user's followers IDs", description = "Fetches the list of IDs of users who follow the authenticated userInfo.")
    @ApiResponse(responseCode = "200", description = "Followers IDs retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = String[].class)))
    @GetMapping("/me/followers/ids")
    public ResponseEntity<List<String>> getMyFollowersIds(Principal principal) {
        return ResponseEntity.ok(userFollowerService.getFollowersIds(principal.getName()));
    }

    @Operation(summary = "Get current userInfo's following list", description = "Fetches the list of users whom the authenticated userInfo is following.")
    @ApiResponse(responseCode = "200", description = "Following list retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/me/following")
    public List<UserInfoDto> getMyFollowing(Principal principal) {
        return userFollowerService.getFollowing(principal.getName());
    }

    // --- Current userInfo follow/unfollow actions ---
    @Operation(summary = "Follow a userInfo", description = "Allows the authenticated userInfo to follow the specified userInfo.")
    @ApiResponse(responseCode = "200", description = "User followed successfully")
    @ApiResponse(responseCode = "400", description = "Cannot follow yourself", content = @Content(mediaType = "application/json"))
    @PostMapping("/{userId}/follow")
    public ResponseEntity<?> followUser(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) {
            return ResponseEntity.badRequest().body("You cannot follow yourself.");
        }
        userFollowerService.followUser(userId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unfollow a user", description = "Allows the authenticated user to unfollow the specified user.")
    @ApiResponse(responseCode = "200", description = "User unfollowed successfully")
    @ApiResponse(responseCode = "400", description = "Cannot unfollow yourself", content = @Content(mediaType = "application/json"))
    @DeleteMapping("/{userId}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) {
            return ResponseEntity.badRequest().body("You cannot unfollow yourself.");
        }
        userFollowerService.unfollowUser(userId, principal.getName());
        return ResponseEntity.ok().build();
    }

    // --- Other users' followers/following lists ---
    @Operation(summary = "Get followers of a user", description = "Fetches the list of users who follow the specified userInfo.")
    @ApiResponse(responseCode = "200", description = "Followers list retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/{userId}/followers")
    public List<UserInfoDto> getFollowers(@PathVariable String userId) {
        return userFollowerService.getFollowers(userId);
    }

    @Operation(summary = "Get following users of a user", description = "Fetches the list of users whom the specified userInfo is following.")
    @ApiResponse(responseCode = "200", description = "Following list retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class)))
    @GetMapping("/{userId}/following")
    public List<UserInfoDto> getFollowing(@PathVariable String userId) {
        return userFollowerService.getFollowing(userId);
    }
}
