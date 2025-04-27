package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.mapper.PostMapper;
import com.uzh.ase.dailygrind.postservice.post.repository.DailyPostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.DailyPostEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${post.ttlMinutes:false}")
    private boolean ttlMinutes;

    private final PostRepository postRepository;
    private final DailyPostRepository dailyPostRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;

    public PostDto createPost(PostDto postDto, String userId) {
        PostEntity postEntity = postMapper.toPostEntity(userId, postDto);
        postEntity.setPostTimestamp(String.valueOf(System.currentTimeMillis()));
        String postId = dailyPostRepository.findDailyPostForUser(userId);
        if (postId != null) {
            throw new IllegalStateException("You already have a daily post for today");
        }
        postRepository.savePost(postEntity);
        DailyPostEntity dailyPostEntity = new DailyPostEntity(userId, postEntity.getPostId(), ttlMinutes);
        dailyPostRepository.saveDailyPost(dailyPostEntity);
        userServiceClient.getFriends()
            .subscribe(friendIds -> {
                for (String friendId : friendIds) {
                    postRepository.saveTimelineEntity(friendId, postEntity.getSk());
                }
            });
        return postMapper.toPostDto(postEntity);
    }

    public List<PostDto> getPostsForUser(String userId) {
        List<PostEntity> postEntities = postRepository.findAllPostsForUser(userId);
        return postEntities.stream()
            .map(postMapper::toPostDto)
            .toList();
    }

    public PostDto getPostById(String postId) {
        PostEntity postEntity = postRepository.findPostById(postId);
        return postMapper.toPostDto(postEntity);
    }

    public PostDto updatePost(String postId, String userId, PostDto postDto) {
        postRepository.savePost(postMapper.toPostEntity(userId, postDto));
        return postMapper.toPostDto(postRepository.findPostById(postId));
    }

    public void deletePost(String postId, String userId) {
        postRepository.deletePostById(postId, userId);
    }

    public void likePost(String postId, String userId) {
        postRepository.likePost(postId, userId);
    }

    public void unlikePost(String postId, String userId) {
        postRepository.unlikePost(postId, userId);
    }

    public CommentDto commentPost(String postId, String userId, CommentDto comment) {
        CommentEntity commentEntity = postMapper.toCommentEntity(userId, postId, comment);
        commentEntity.setCommentTimestamp(String.valueOf(System.currentTimeMillis()));
        postRepository.saveComment(commentEntity);
        return postMapper.toCommentDto(commentEntity);
    }

    public void deleteComment(String postId, String commentId, String userId) {
        postRepository.deleteComment(postId, commentId, userId);
    }

    public List<PostDto> getTimelineForUser(String userId) {
        List<String> postIds = postRepository.getTimelineForUser(userId);
        List<PostEntity> postEntities = postIds.stream().map(postRepository::findPostById).toList();
        return postEntities.stream()
            .map(postMapper::toPostDto)
            .toList();
    }

    public PostDto getDailyPostForUser(String userId) {
        String postId = dailyPostRepository.findDailyPostForUser(userId);
        PostEntity postEntity = postRepository.findPostById(postId);
        return postMapper.toPostDto(postEntity);
    }

    public List<CommentDto> getComments(String postId) {
        PostEntity postEntity = postRepository.findPostById(postId);
        String userId = postEntity.getUserId();
        List<CommentEntity> commentEntities = postRepository.findAllCommentsForPost(userId, postId);
        return commentEntities.stream()
            .map(postMapper::toCommentDto)
            .toList();
    }
}
