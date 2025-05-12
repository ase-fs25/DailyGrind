package com.uzh.ase.dailygrind.postservice.post.controller;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentEntryDto;
import com.uzh.ase.dailygrind.postservice.post.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controller class for managing comments on posts.
 * <p>
 * This class exposes endpoints to get, add, and delete comments for posts.
 * All methods are secured and require proper user authentication.
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Retrieves all comments for a specific post.
     *
     * @param postId the unique identifier of the post
     * @return a list of CommentEntryDto objects representing the comments of the post
     */
    @Operation(summary = "Get all comments for a post")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved comments")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentEntryDto>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    /**
     * Adds a comment to a specific post.
     *
     * @param postId the unique identifier of the post
     * @param comment the comment data to be added
     * @param principal the authenticated user performing the action
     * @return the created CommentEntryDto object
     */
    @Operation(summary = "Add a comment to a post")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentEntryDto> commentPost(@PathVariable String postId, @RequestBody CommentDto comment, Principal principal) {
        CommentEntryDto commentDto = commentService.commentPost(postId, principal.getName(), comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }

    /**
     * Deletes a specific comment from a post.
     *
     * @param postId the unique identifier of the post
     * @param commentId the unique identifier of the comment to be deleted
     * @param principal the authenticated user performing the deletion
     * @return a ResponseEntity indicating the deletion status
     */
    @Operation(summary = "Delete a comment by ID from a post")
    @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String postId, @PathVariable String commentId, Principal principal) {
        commentService.deleteComment(postId, commentId, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
