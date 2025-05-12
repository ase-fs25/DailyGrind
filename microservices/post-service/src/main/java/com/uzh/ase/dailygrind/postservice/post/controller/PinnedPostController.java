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

@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class PinnedPostController {

    private final PostService postService;

    @Operation(summary = "Getting all of my pinned posts")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved pinned posts")
    @GetMapping("/users/me/pinned-posts")
    public ResponseEntity<List<PostDto>> getMyPinnedPosts(Principal principal) {
        List<PostDto> pinnedPosts = postService.getPinnedPostsByUserId(principal.getName());
        return ResponseEntity.ok(pinnedPosts);
    }

    @Operation(summary = "Getting all pinned posts for user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved pinned posts")
    @GetMapping("/users/{userId}/pinned-posts")
    public ResponseEntity<List<PostDto>> getPinnedPostsByUserId(@PathVariable String userId) {
        List<PostDto> pinnedPosts = postService.getPinnedPostsByUserId(userId);
        return ResponseEntity.ok(pinnedPosts);
    }

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

    @Operation(summary = "Removing pinned post")
    @ApiResponse(responseCode = "204", description = "Post unpinned successfully")
    @DeleteMapping("/users/me/pinned-posts/{postId}")
    public ResponseEntity<PostDto> unpinPost(@PathVariable String postId, Principal principal) {
        postService.unpinPost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
