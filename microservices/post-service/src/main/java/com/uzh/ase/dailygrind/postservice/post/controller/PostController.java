package com.uzh.ase.dailygrind.postservice.post.controller;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/me/posts")
    public ResponseEntity<List<PostDto>> getMyPosts(Principal principal) {
        return ResponseEntity.ok(postService.getPostsForUser(principal.getName()));
    }

    @GetMapping("/me/timeline")
    public ResponseEntity<List<PostDto>> getMyTimeline(Principal principal) {
        return ResponseEntity.ok(postService.getTimelineForUser(principal.getName()));
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable String userId) {
        return ResponseEntity.ok(postService.getPostsForUser(userId));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable String postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @PostMapping("/posts")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(postDto, principal.getName()));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable String postId, @RequestBody PostDto postDto, Principal principal) {
        if (!postId.equals(postDto.postId())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Post ID in URL and body do not match");
        return ResponseEntity.ok(postService.updatePost(postId, principal.getName(), postDto));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId, Principal principal) {
        postService.deletePost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable String postId, Principal principal) {
        postService.likePost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable String postId, Principal principal) {
        postService.unlikePost(postId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<Void> commentPost(@PathVariable String postId, @RequestBody CommentDto comment, Principal principal) {
        postService.commentPost(postId, principal.getName(), comment);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String postId, @PathVariable String commentId, Principal principal) {
        postService.deleteComment(postId, commentId, principal.getName());
        return ResponseEntity.noContent().build();
    }

}