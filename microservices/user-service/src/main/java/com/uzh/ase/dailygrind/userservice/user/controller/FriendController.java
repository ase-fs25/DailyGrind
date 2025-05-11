package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.service.UserFriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class FriendController {

    private final UserFriendService userFriendService;

    @Operation(summary = "Send a friend request", description = "Send a friend request to another user.")
    @ApiResponse(responseCode = "200", description = "Friend request sent successfully")
    @PostMapping("/requests")
    public ResponseEntity<String> sendFriendRequest(@RequestParam String targetUserId, Principal principal) {
        if (Objects.equals(targetUserId, principal.getName())) return ResponseEntity.badRequest().body("You cannot send a friend request to yourself.");
        userFriendService.sendFriendRequest(principal.getName(), targetUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Accept a friend request", description = "Accept an incoming friend request.")
    @ApiResponse(responseCode = "200", description = "Friend request accepted successfully")
    @PostMapping("/requests/{friendRequestSenderUserId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable String friendRequestSenderUserId, Principal principal) {
        userFriendService.acceptFriendRequest(principal.getName(), friendRequestSenderUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Decline a friend request", description = "Decline an incoming friend request.")
    @ApiResponse(responseCode = "200", description = "Friend request declined successfully")
    @DeleteMapping("/requests/{friendRequestSenderUserId}/decline")
    public ResponseEntity<Void> declineFriendRequest(@PathVariable String friendRequestSenderUserId, Principal principal) {
        userFriendService.declineFriendRequest(principal.getName(), friendRequestSenderUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cancel a sent friend request", description = "Cancel an outgoing friend request.")
    @ApiResponse(responseCode = "200", description = "Friend request cancelled successfully")
    @DeleteMapping("/requests/{friendRequestReceiverUserId}/cancel")
    public ResponseEntity<Void> cancelFriendRequest(@PathVariable String friendRequestReceiverUserId, Principal principal) {
        userFriendService.cancelFriendRequest(principal.getName(), friendRequestReceiverUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List incoming friend requests", description = "List all incoming friend requests for the current user.")
    @ApiResponse(responseCode = "200", description = "Incoming friend requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/requests/incoming")
    public List<UserInfoDto> listIncomingRequests(Principal principal) {
        return userFriendService.getIncomingFriendRequests(principal.getName());
    }

    @Operation(summary = "List outgoing friend requests", description = "List all outgoing friend requests sent by the current user.")
    @ApiResponse(responseCode = "200", description = "Outgoing friend requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/requests/outgoing")
    public List<UserInfoDto> listOutgoingRequests(Principal principal) {
        return userFriendService.getOutgoingFriendRequests(principal.getName());
    }

    @Operation(summary = "List friends", description = "List all friends of the current user.")
    @ApiResponse(responseCode = "200", description = "Friends list retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/me/friends")
    public List<UserInfoDto> listFriends(Principal principal) {
        return userFriendService.getFriends(principal.getName(), principal.getName());
    }

    @Operation(summary = "List friends", description = "List all friends of userId")
    @ApiResponse(responseCode = "200", description = "Friends list retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/{userId}/friends")
    public List<UserInfoDto> listFriends(@PathVariable String userId, Principal principal) {
        return userFriendService.getFriends(userId, principal.getName());
    }

    @Operation(summary = "Remove a friend", description = "Remove a user from your friends list.")
    @ApiResponse(responseCode = "200", description = "Friend removed successfully")
    @DeleteMapping("/me/friends/{friendToRemoveUserId}/remove")
    public ResponseEntity<Void> removeFriend(@PathVariable String friendToRemoveUserId, Principal principal) {
        userFriendService.removeFriend(principal.getName(), friendToRemoveUserId);
        return ResponseEntity.ok().build();
    }
}
