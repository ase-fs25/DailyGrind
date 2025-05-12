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

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "Get today's daily post for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved daily post")
    @GetMapping("/users/me/daily-post")
    public ResponseEntity<PostDto> getMyDailyPost(Principal principal) {
        return ResponseEntity.ok(postService.getDailyPostForUser(principal.getName(), principal.getName()));
    }

    @Operation(summary = "Get today's daily post for a specific user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's daily post")
    @GetMapping("/users/{userId}/daily-post")
    public ResponseEntity<PostDto> getDailyPostForUser(@PathVariable String userId, Principal principal) {
        return ResponseEntity.ok(postService.getDailyPostForUser(userId, principal.getName()));
    }

    @Operation(summary = "Get all posts by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved posts")
    @GetMapping("/users/me/posts")
    public ResponseEntity<List<PostDto>> getMyPosts(Principal principal) {
        return ResponseEntity.ok(postService.getPostsForUser(principal.getName()));
    }

    @Operation(summary = "Get all posts by a specific user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's posts")
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable String userId) {
        return ResponseEntity.ok(postService.getPostsForUser(userId));
    }

    @Operation(summary = "Get a post by ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved post")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable String postId, Principal principal) {
        return ResponseEntity.ok(postService.getPostById(postId, principal.getName()));
    }

    @Operation(summary = "Create a new post for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Post created successfully"),
        @ApiResponse(responseCode = "400", description = "Daily post already exists for today")
    })
    @PostMapping("/posts")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, Principal principal) {
        postDto = postService.createPost(postDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(postDto);
    }

    @Operation(summary = "Update a post by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post updated successfully"),
        @ApiResponse(responseCode = "400", description = "Post ID mismatch between URL and body")
    })
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable String postId, @RequestBody PostDto postDto, Principal principal) {
        if (!postId.equals(postDto.postId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post ID mismatch");
        return ResponseEntity.ok(postService.updatePost(postId, principal.getName(), postDto));
    }

    @Operation(summary = "Delete a post by ID")
    @ApiResponse(responseCode = "204", description = "Post deleted successfully")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId, Principal principal) {
        postService.deletePost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Like a post")
    @ApiResponse(responseCode = "201", description = "Post liked successfully")
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> likePost(@PathVariable String postId, Principal principal) {
        postService.likePost(postId, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Unlike a post")
    @ApiResponse(responseCode = "204", description = "Post unliked successfully")
    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> unlikePost(@PathVariable String postId, Principal principal) {
        postService.unlikePost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
