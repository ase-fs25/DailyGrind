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
 * Controller responsible for handling comment-related operations on posts.
 * <p>
 * This class provides the API for retrieving, adding, and deleting comments on posts.
 * The controller interacts with the {@link CommentService} to manage comment data.
 */
@RestController
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Retrieves all comments for a given post.
     *
     * @param postId the ID of the post to retrieve comments for
     * @return a list of {@link CommentEntryDto} objects containing comment data for the specified post
     */
    @Operation(summary = "Get all comments for a post")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved comments")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentEntryDto>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    /**
     * Adds a new comment to a specified post.
     *
     * @param postId  the ID of the post to comment on
     * @param comment the content of the comment
     * @param principal the current authenticated user
     * @return the created {@link CommentEntryDto} containing the comment data
     */
    @Operation(summary = "Add a comment to a post")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentEntryDto> commentPost(@PathVariable String postId, @RequestBody CommentDto comment, Principal principal) {
        CommentEntryDto commentDto = commentService.commentPost(postId, principal.getName(), comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }

    /**
     * Deletes a comment from a post.
     *
     * @param postId     the ID of the post
     * @param commentId  the ID of the comment to delete
     * @param principal  the current authenticated user
     * @return a response with status code 204 indicating successful deletion
     */
    @Operation(summary = "Delete a comment by ID from a post")
    @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String postId, @PathVariable String commentId, Principal principal) {
        commentService.deleteComment(postId, commentId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
