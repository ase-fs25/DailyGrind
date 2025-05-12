package com.uzh.ase.dailygrind.postservice.post.controller;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controller responsible for handling pinned post-related operations.
 * <p>
 * This controller allows users to pin and unpin posts, and retrieve pinned posts.
 * It interacts with the {@link PostService} to manage pinned posts for a user.
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PinnedPostController {

    private final PostService postService;

    /**
     * Retrieves all pinned posts for the currently authenticated user.
     *
     * @param principal the current authenticated user
     * @return a list of {@link PostDto} objects containing the pinned posts for the user
     */
    @Operation(summary = "Getting all of my pinned posts")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved pinned posts")
    @GetMapping("/users/me/pinned-posts")
    public ResponseEntity<List<PostDto>> getMyPinnedPosts(Principal principal) {
        List<PostDto> pinnedPosts = postService.getPinnedPostsByUserId(principal.getName());
        return ResponseEntity.ok(pinnedPosts);
    }

    /**
     * Retrieves all pinned posts for a specified user.
     *
     * @param userId the ID of the user whose pinned posts are to be retrieved
     * @return a list of {@link PostDto} objects containing the pinned posts for the specified user
     */
    @Operation(summary = "Getting all pinned posts for user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved pinned posts")
    @GetMapping("/users/{userId}/pinned-posts")
    public ResponseEntity<List<PostDto>> getPinnedPostsByUserId(@PathVariable String userId) {
        List<PostDto> pinnedPosts = postService.getPinnedPostsByUserId(userId);
        return ResponseEntity.ok(pinnedPosts);
    }

    /**
     * Pins a post for the currently authenticated user.
     *
     * @param postId the ID of the post to pin
     * @param principal the current authenticated user
     * @return the pinned {@link PostDto} object if successful, or a bad request response if the post is already pinned
     */
    @Operation(summary = "Adding new pinned post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post pinned successfully"),
        @ApiResponse(responseCode = "400", description = "Post already pinned")
    })
    @PostMapping("/users/me/pinned-posts/{postId}")
    public ResponseEntity<?> pinPost(@PathVariable String postId, Principal principal) {
        try {
            PostDto postDto = postService.pinPost(postId, principal.getName());
            return ResponseEntity.ok(postDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Unpins a post for the currently authenticated user.
     *
     * @param postId the ID of the post to unpin
     * @param principal the current authenticated user
     * @return a response with status code 204 indicating successful unpinning
     */
    @Operation(summary = "Removing pinned post")
    @ApiResponse(responseCode = "204", description = "Post unpinned successfully")
    @DeleteMapping("/users/me/pinned-posts/{postId}")
    public ResponseEntity<PostDto> unpinPost(@PathVariable String postId, Principal principal) {
        postService.unpinPost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
