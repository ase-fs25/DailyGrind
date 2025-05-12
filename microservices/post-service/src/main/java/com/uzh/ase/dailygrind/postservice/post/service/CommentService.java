package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentEntryDto;
import com.uzh.ase.dailygrind.postservice.post.mapper.CommentMapper;
import com.uzh.ase.dailygrind.postservice.post.repository.CommentRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<CommentEntryDto> getComments(String postId) {
        PostEntity postEntity = postRepository.findPostById(postId);
        if (postEntity == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found");
        String userId = postEntity.getUserId();
        List<CommentEntity> commentEntities = commentRepository.findAllCommentsForPost(postId);
        return commentEntities.stream()
            .map(commentEntity -> new CommentEntryDto(
                commentMapper.toCommentDto(commentEntity),
                userService.getUser(userId)
            ))
            .toList();
    }

    public CommentEntryDto commentPost(String postId, String userId, CommentDto comment) {
        CommentEntity commentEntity = commentMapper.toCommentEntity(userId, postId, comment);
        commentEntity.setCommentTimestamp(String.valueOf(System.currentTimeMillis()));
        commentRepository.saveComment(commentEntity);

        PostEntity postEntity = postRepository.findPostById(postId);
        postEntity.setCommentCount(postEntity.getCommentCount() + 1);
        postRepository.savePost(postEntity);

        return new CommentEntryDto(comment, userService.getUser(userId));
    }

    public void deleteComment(String postId, String commentId, String userId) {
        commentRepository.deleteComment(postId, commentId, userId);

        PostEntity postEntity = postRepository.findPostById(postId);
        postEntity.setCommentCount(postEntity.getCommentCount() - 1);
        postRepository.savePost(postEntity);
    }

}
