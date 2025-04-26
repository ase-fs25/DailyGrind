package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.mapper.PostMapper;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final DailyPostRepository dailyPostRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;

    public PostDto createPost(PostDto postDto, String userId) {
        PostEntity postEntity = postMapper.toPostEntity(userId, postDto);
        postRepository.savePost(postEntity);
        postRepository.saveDailyPost(postEntity);
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
        String pk = PostEntity.PREFIX + "#" + userId + "#" + PostEntity.POSTFIX;
        String sk = PostEntity.POSTFIX + "#" + postId;
        postRepository.deleteEntity(pk, sk);
    }

    public void likePost(String postId, String userId) {
        postRepository.likePost(postId, userId);
    }

    public void unlikePost(String postId, String userId) {
        postRepository.unlikePost(postId, userId);
    }

    public void commentPost(String postId, String userId, CommentDto comment) {
        CommentEntity commentEntity = postMapper.toCommentEntity(userId, postId, comment);
        postRepository.saveComment(commentEntity);
    }

    public void deleteComment(String postId, String commentId, String userId) {
        String pk = PostEntity.POSTFIX + "#" + postId + "#" + CommentEntity.ID_NAME;
        String sk = PostEntity.PREFIX + "#" + userId + "#" + CommentEntity.ID_NAME + "#" + commentId;
        postRepository.deleteEntity(pk, sk);
    }

    public List<PostDto> getTimelineForUser(String userId) {
        List<String> postIds = postRepository.getTimelineForUser(userId);
        List<PostEntity> postEntities = postIds.stream().map(postRepository::findPostById).toList();
        return postEntities.stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public PostDto getDailyPostForUser(String userId) {
        PostEntity postEntity = postRepository.findDailyPostForUser(userId);
        return postMapper.toPostDto(postEntity);
    }
}
