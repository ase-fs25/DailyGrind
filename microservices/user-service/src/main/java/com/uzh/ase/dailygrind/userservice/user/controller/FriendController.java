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

/**
 * Controller for handling friend-related operations.
 * <p>
 * This controller exposes endpoints for sending, accepting, declining, and canceling friend requests,
 * as well as listing friends, incoming requests, and outgoing requests for users.
 * </p>
 */
@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class FriendController {

    private final UserFriendService userFriendService;

    /**
     * Sends a friend request to another user.
     *
     * @param targetUserId the ID of the user to send the friend request to
     * @param principal the authenticated user's principal
     * @return a response indicating whether the request was successful or not
     */
    @Operation(summary = "Send a friend request", description = "Send a friend request to another user.")
    @ApiResponse(responseCode = "200", description = "Friend request sent successfully")
    @PostMapping("/requests")
    public ResponseEntity<String> sendFriendRequest(@RequestParam String targetUserId, Principal principal) {
        if (Objects.equals(targetUserId, principal.getName())) return ResponseEntity.badRequest().body("You cannot send a friend request to yourself.");
        userFriendService.sendFriendRequest(principal.getName(), targetUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * Accepts an incoming friend request.
     *
     * @param friendRequestSenderUserId the ID of the user who sent the friend request
     * @param principal the authenticated user's principal
     * @return a response indicating that the request was accepted
     */
    @Operation(summary = "Accept a friend request", description = "Accept an incoming friend request.")
    @ApiResponse(responseCode = "200", description = "Friend request accepted successfully")
    @PostMapping("/requests/{friendRequestSenderUserId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable String friendRequestSenderUserId, Principal principal) {
        userFriendService.acceptFriendRequest(principal.getName(), friendRequestSenderUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * Declines an incoming friend request.
     *
     * @param friendRequestSenderUserId the ID of the user who sent the friend request
     * @param principal the authenticated user's principal
     * @return a response indicating that the request was declined
     */
    @Operation(summary = "Decline a friend request", description = "Decline an incoming friend request.")
    @ApiResponse(responseCode = "200", description = "Friend request declined successfully")
    @DeleteMapping("/requests/{friendRequestSenderUserId}/decline")
    public ResponseEntity<Void> declineFriendRequest(@PathVariable String friendRequestSenderUserId, Principal principal) {
        userFriendService.declineFriendRequest(principal.getName(), friendRequestSenderUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * Cancels a sent friend request.
     *
     * @param friendRequestReceiverUserId the ID of the user to whom the friend request was sent
     * @param principal the authenticated user's principal
     * @return a response indicating that the request was canceled
     */
    @Operation(summary = "Cancel a sent friend request", description = "Cancel an outgoing friend request.")
    @ApiResponse(responseCode = "200", description = "Friend request cancelled successfully")
    @DeleteMapping("/requests/{friendRequestReceiverUserId}/cancel")
    public ResponseEntity<Void> cancelFriendRequest(@PathVariable String friendRequestReceiverUserId, Principal principal) {
        userFriendService.cancelFriendRequest(principal.getName(), friendRequestReceiverUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * Lists all incoming friend requests for the current user.
     *
     * @param principal the authenticated user's principal
     * @return a list of incoming friend requests
     */
    @Operation(summary = "List incoming friend requests", description = "List all incoming friend requests for the current user.")
    @ApiResponse(responseCode = "200", description = "Incoming friend requests retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/requests/incoming")
    public List<UserInfoDto> listIncomingRequests(Principal principal) {
        return userFriendService.getIncomingFriendRequests(principal.getName());
    }

    /**
     * Lists all outgoing friend requests sent by the current user.
     *
     * @param principal the authenticated user's principal
     * @return a list of outgoing friend requests
     */
    @Operation(summary = "List outgoing friend requests", description = "List all outgoing friend requests sent by the current user.")
    @ApiResponse(responseCode = "200", description = "Outgoing friend requests retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/requests/outgoing")
    public List<UserInfoDto> listOutgoingRequests(Principal principal) {
        return userFriendService.getOutgoingFriendRequests(principal.getName());
    }

    /**
     * Lists all friends of the current user.
     *
     * @param principal the authenticated user's principal
     * @return a list of the current user's friends
     */
    @Operation(summary = "List friends", description = "List all friends of the current user.")
    @ApiResponse(responseCode = "200", description = "Friends list retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/me/friends")
    public List<UserInfoDto> listFriends(Principal principal) {
        return userFriendService.getFriends(principal.getName(), principal.getName());
    }

    /**
     * Lists all friends of the specified user.
     *
     * @param userId the ID of the user whose friends are to be listed
     * @param principal the authenticated user's principal
     * @return a list of the specified user's friends
     */
    @Operation(summary = "List friends", description = "List all friends of userId")
    @ApiResponse(responseCode = "200", description = "Friends list retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto[].class)))
    @GetMapping("/{userId}/friends")
    public List<UserInfoDto> listFriends(@PathVariable String userId, Principal principal) {
        return userFriendService.getFriends(userId, principal.getName());
    }

    /**
     * Removes a user from the current user's friends list.
     *
     * @param friendToRemoveUserId the ID of the user to be removed from the friends list
     * @param principal the authenticated user's principal
     * @return a response indicating that the friend was removed
     */
    @Operation(summary = "Remove a friend", description = "Remove a user from your friends list.")
    @ApiResponse(responseCode = "200", description = "Friend removed successfully")
    @DeleteMapping("/me/friends/{friendToRemoveUserId}/remove")
    public ResponseEntity<Void> removeFriend(@PathVariable String friendToRemoveUserId, Principal principal) {
        userFriendService.removeFriend(principal.getName(), friendToRemoveUserId);
        return ResponseEntity.ok().build();
    }
}
