package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.mapper.PostMapper;
import com.uzh.ase.dailygrind.postservice.post.repository.CommentRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.DailyPostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PinnedPostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final DailyPostRepository dailyPostRepository;
    private final PinnedPostRepository pinnedPostRepository;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;

    public PostDto createPost(PostDto postDto, String userId) {
        PostEntity postEntity = postMapper.toPostEntity(userId, postDto);
        postEntity.setPostTimestamp(String.valueOf(System.currentTimeMillis()));
        String postId = dailyPostRepository.findDailyPostForUser(userId);
        if (postId != null) {
            throw new IllegalStateException("You already have a daily post for today");
        }
        postRepository.savePost(postEntity);
        DailyPostEntity dailyPostEntity = new DailyPostEntity(userId, postEntity.getPostId());
        dailyPostRepository.saveDailyPost(dailyPostEntity);
        return postMapper.toPostDto(postEntity, false, false);
    }

    public PostDto addIsLikedAndIsPinnedToPostDto(PostEntity postEntity, String userId) {
        List<String> pinnedPostIds = pinnedPostRepository.findPinnedPostIdsForUser(userId);
        if (postEntity == null) return null;
        return postMapper.toPostDto(
            postEntity,
            postRepository.findAllUsersWhoLikedPost(postEntity.getPostId()).contains(userId),
            pinnedPostIds.contains(postEntity.getPostId()));
    }

    public List<PostDto> getPostsForUser(String userId) {
        List<PostEntity> postEntities = postRepository.findAllPostsForUser(userId);
        return postEntities.stream()
            .map(postEntity -> addIsLikedAndIsPinnedToPostDto(postEntity, userId))
            .toList();
    }

    public PostDto getPostById(String postId, String userId) {
        PostEntity postEntity = postRepository.findPostById(postId);
        return addIsLikedAndIsPinnedToPostDto(postEntity, userId);
    }

    public PostDto updatePost(String postId, String userId, PostDto postDto) {
        PostEntity postEntity = postMapper.toPostEntity(userId, postDto);
        postRepository.savePost(postEntity);
        return addIsLikedAndIsPinnedToPostDto(postEntity, userId);
    }

    public void deletePost(String postId, String userId) {
        unpinPost(postId, userId);
        postRepository.deletePostById(postId, userId);
        postRepository.deleteLikesForPost(postId);
        dailyPostRepository.deleteDailyPostById(postId, userId);
        commentRepository.deleteAllCommentsForPost(postId, userId);
    }

    public List<String> findAllUsersWhoLikedPost(String postId) {
        return postRepository.findAllUsersWhoLikedPost(postId);
    }

    public void likePost(String postId, String userId) {
        LikeEntity likeEntity = postMapper.toLikeEntity(postId, userId);
        postRepository.likePost(likeEntity);
    }

    public void unlikePost(String postId, String userId) {
        LikeEntity likeEntity = postMapper.toLikeEntity(postId, userId);
        postRepository.unlikePost(likeEntity);
    }

    public PostDto getDailyPostForUser(String userId) {
        String postId = dailyPostRepository.findDailyPostForUser(userId);
        PostEntity postEntity = postRepository.findPostById(postId);
        return addIsLikedAndIsPinnedToPostDto(postEntity, userId);
    }

    public List<PostDto> getPinnedPostsByUserId(String userId) {
        List<String> pinnedPostEntities = pinnedPostRepository.findPinnedPostIdsForUser(userId);
        return pinnedPostEntities.stream()
            .map(postRepository::findPostById)
            .map(postEntity -> addIsLikedAndIsPinnedToPostDto(postEntity, userId))
            .toList();
    }

    public PostDto pinPost(String postId, String userId) {
        PostEntity postEntity = postRepository.findPostById(postId);
        if (postEntity == null) throw new NoSuchElementException("Post with id " + postId + " does not exist");
        PinnedPostEntity pinnedPostEntity = new PinnedPostEntity(userId, postEntity.getPostId());
        pinnedPostRepository.savePinnedPost(pinnedPostEntity);
        return addIsLikedAndIsPinnedToPostDto(postEntity, userId);
    }

    public void unpinPost(String postId, String userId) {
        PinnedPostEntity pinnedPostEntity = new  PinnedPostEntity(userId, postId);
        pinnedPostRepository.deleteDailyPostById(pinnedPostEntity);
    }
}
