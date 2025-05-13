package com.uzh.ase.dailygrind.postservice.post.controller;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * Controller responsible for handling post-related operations.
 * <p>
 * This controller allows users to create, update, delete, like, and retrieve posts, as well as manage daily posts.
 * It interacts with the {@link PostService} to perform these actions.
 */
@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * Retrieves today's daily post for the currently authenticated user.
     *
     * @param principal the current authenticated user
     * @return the daily post for the user
     */
    @Operation(summary = "Get today's daily post for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved daily post")
    @GetMapping("/users/me/daily-post")
    public ResponseEntity<PostDto> getMyDailyPost(Principal principal) {
        return ResponseEntity.ok(postService.getDailyPostForUser(principal.getName(), principal.getName()));
    }

    /**
     * Retrieves today's daily post for a specific user.
     *
     * @param userId the ID of the user whose daily post is to be retrieved
     * @param principal the current authenticated user
     * @return the daily post for the specified user
     */
    @Operation(summary = "Get today's daily post for a specific user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's daily post")
    @GetMapping("/users/{userId}/daily-post")
    public ResponseEntity<PostDto> getDailyPostForUser(@PathVariable String userId, Principal principal) {
        return ResponseEntity.ok(postService.getDailyPostForUser(userId, principal.getName()));
    }

    /**
     * Retrieves all posts created by the currently authenticated user.
     *
     * @param principal the current authenticated user
     * @return a list of posts created by the user
     */
    @Operation(summary = "Get all posts by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved posts")
    @GetMapping("/users/me/posts")
    public ResponseEntity<List<PostDto>> getMyPosts(Principal principal) {
        return ResponseEntity.ok(postService.getPostsForUser(principal.getName()));
    }

    /**
     * Retrieves all posts created by a specific user.
     *
     * @param userId the ID of the user whose posts are to be retrieved
     * @return a list of posts created by the specified user
     */
    @Operation(summary = "Get all posts by a specific user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's posts")
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable String userId) {
        return ResponseEntity.ok(postService.getPostsForUser(userId));
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param postId the ID of the post to retrieve
     * @param principal the current authenticated user
     * @return the post with the given ID
     */
    @Operation(summary = "Get a post by ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved post")
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable String postId, Principal principal) {
        return ResponseEntity.ok(postService.getPostById(postId, principal.getName()));
    }

    /**
     * Creates a new post for the currently authenticated user.
     *
     * @param postDto the post data to create
     * @param principal the current authenticated user
     * @return the created post
     */
    @Operation(summary = "Create a new post for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Post created successfully"),
        @ApiResponse(responseCode = "400", description = "Daily post already exists for today")
    })
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, Principal principal) {
        postDto = postService.createPost(postDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(postDto);
    }

    /**
     * Updates a post by its ID.
     *
     * @param postId the ID of the post to update
     * @param postDto the new post data
     * @param principal the current authenticated user
     * @return the updated post
     * @throws ResponseStatusException if the post ID in the URL does not match the one in the body
     */
    @Operation(summary = "Update a post by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post updated successfully"),
        @ApiResponse(responseCode = "400", description = "Post ID mismatch between URL and body")
    })
    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable String postId, @RequestBody PostDto postDto, Principal principal) {
        if (!postId.equals(postDto.postId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post ID mismatch");
        return ResponseEntity.ok(postService.updatePost(postId, principal.getName(), postDto));
    }

    /**
     * Deletes a post by its ID.
     *
     * @param postId the ID of the post to delete
     * @param principal the current authenticated user
     * @return a response with status code 204 indicating successful deletion
     */
    @Operation(summary = "Delete a post by ID")
    @ApiResponse(responseCode = "204", description = "Post deleted successfully")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId, Principal principal) {
        postService.deletePost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Likes a post.
     *
     * @param postId the ID of the post to like
     * @param principal the current authenticated user
     * @return a response with status code 201 indicating successful like
     */
    @Operation(summary = "Like a post")
    @ApiResponse(responseCode = "201", description = "Post liked successfully")
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> likePost(@PathVariable String postId, Principal principal) {
        postService.likePost(postId, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Unlikes a post.
     *
     * @param postId the ID of the post to unlike
     * @param principal the current authenticated user
     * @return a response with status code 204 indicating successful unlike
     */
    @Operation(summary = "Unlike a post")
    @ApiResponse(responseCode = "204", description = "Post unliked successfully")
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlikePost(@PathVariable String postId, Principal principal) {
        postService.unlikePost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
