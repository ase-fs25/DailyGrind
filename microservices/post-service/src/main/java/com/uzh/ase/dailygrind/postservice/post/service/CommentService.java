package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentEntryDto;
import com.uzh.ase.dailygrind.postservice.post.mapper.CommentMapper;
import com.uzh.ase.dailygrind.postservice.post.repository.CommentRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service for managing comments on posts.
 * <p>
 * This class provides methods to create, retrieve, and delete comments on posts.
 * It also handles updating the comment count on the associated post when a comment is added or deleted.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * Retrieves all comments for a given post.
     * <p>
     * This method retrieves all comments associated with a specific post and maps them into
     * {@link CommentEntryDto} objects. It also retrieves the user information for each comment.
     *
     * @param postId the ID of the post for which to retrieve comments
     * @return a list of {@link CommentEntryDto} objects representing the comments
     * @throws ResponseStatusException if the post does not exist
     */
    public List<CommentEntryDto> getComments(String postId) {
        log.info("Retrieving comments for post with ID '{}'", postId);
        PostEntity postEntity = postRepository.findPostById(postId);
        if (postEntity == null) {
            log.warn("Post with ID '{}' not found", postId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found");
        }

        List<CommentEntity> commentEntities = commentRepository.findAllCommentsForPost(postId);
        log.info("Found {} comments for post '{}'", commentEntities.size(), postId);

        return commentEntities.stream()
            .map(commentEntity -> new CommentEntryDto(
                commentMapper.toCommentDto(commentEntity),
                userService.getUser(commentEntity.getUserId())
            ))
            .toList();
    }

    /**
     * Adds a comment to a specific post.
     * <p>
     * This method creates a new comment entity, saves it in the comment repository,
     * and updates the comment count of the associated post. It returns the newly added comment
     * along with the user information in the form of a {@link CommentEntryDto}.
     *
     * @param postId the ID of the post to comment on
     * @param userId the ID of the user making the comment
     * @param comment the comment data to add
     * @return a {@link CommentEntryDto} representing the newly added comment
     */
    public CommentEntryDto commentPost(String postId, String userId, CommentDto comment) {
        log.info("User '{}' is commenting on post '{}'", userId, postId);

        CommentEntity commentEntity = commentMapper.toCommentEntity(userId, postId, comment);
        commentEntity.setCommentTimestamp(String.valueOf(System.currentTimeMillis()));
        commentRepository.saveComment(commentEntity);
        log.info("Saved new comment for post '{}'", postId);

        PostEntity postEntity = postRepository.findPostById(postId);
        postEntity.setCommentCount(postEntity.getCommentCount() + 1);
        postRepository.savePost(postEntity);
        log.info("Incremented comment count for post '{}'", postId);

        return new CommentEntryDto(comment, userService.getUser(userId));
    }

    /**
     * Deletes a specific comment on a post.
     * <p>
     * This method deletes the specified comment from the comment repository and updates the
     * comment count of the associated post.
     *
     * @param postId the ID of the post from which to delete the comment
     * @param commentId the ID of the comment to delete
     * @param userId the ID of the user requesting the deletion
     */
    public void deleteComment(String postId, String commentId, String userId) {
        log.info("User '{}' is deleting comment '{}' from post '{}'", userId, commentId, postId);

        commentRepository.deleteComment(postId, commentId, userId);
        log.info("Deleted comment '{}' from post '{}'", commentId, postId);

        PostEntity postEntity = postRepository.findPostById(postId);
        postEntity.setCommentCount(postEntity.getCommentCount() - 1);
        postRepository.savePost(postEntity);
        log.info("Decremented comment count for post '{}'", postId);
    }

}
