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

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Get all comments for a post")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved comments")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentEntryDto>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @Operation(summary = "Add a comment to a post")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentEntryDto> commentPost(@PathVariable String postId, @RequestBody CommentDto comment, Principal principal) {
        CommentEntryDto commentDto = commentService.commentPost(postId, principal.getName(), comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }

    @Operation(summary = "Delete a comment by ID from a post")
    @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String postId, @PathVariable String commentId, Principal principal) {
        commentService.deleteComment(postId, commentId, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
