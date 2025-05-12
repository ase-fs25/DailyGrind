package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.mapper.PostMapper;
import com.uzh.ase.dailygrind.postservice.post.repository.CommentRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.DailyPostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PinnedPostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service layer responsible for handling business logic related to posts.
 * It manages creation, updating, liking, pinning, and deleting posts.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final DailyPostRepository dailyPostRepository;
    private final PinnedPostRepository pinnedPostRepository;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;

    /**
     * Creates a new post for a user.
     *
     * @param postDto  The data transfer object containing the post information.
     * @param userId   The ID of the user creating the post.
     * @return         A PostDto containing the created post's details.
     * @throws ResponseStatusException if the user already has a daily post.
     */
    public PostDto createPost(PostDto postDto, String userId) {
        PostEntity postEntity = postMapper.toPostEntity(userId, postDto);
        postEntity.setPostTimestamp(String.valueOf(System.currentTimeMillis()));
        String postId = dailyPostRepository.findDailyPostForUser(userId);
        if (postId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has a daily post");
        }
        postRepository.savePost(postEntity);
        DailyPostEntity dailyPostEntity = new DailyPostEntity(userId, postEntity.getPostId());
        dailyPostRepository.saveDailyPost(dailyPostEntity);
        return postMapper.toPostDto(postEntity, false, false);
    }

    /**
     * Adds information about whether a post is liked or pinned by a user.
     *
     * @param postEntity  The post entity to be transformed.
     * @param userId      The ID of the user.
     * @return            A PostDto containing post details with like and pin status.
     */
    public PostDto addIsLikedAndIsPinnedToPostDto(PostEntity postEntity, String userId) {
        if (postEntity == null) return null;
        List<String> pinnedPostIds = pinnedPostRepository.findPinnedPostIdsForUser(userId);
        return postMapper.toPostDto(
            postEntity,
            postRepository.findAllUsersWhoLikedPost(postEntity.getPostId()).contains(userId),
            pinnedPostIds.contains(postEntity.getPostId()));
    }

    /**
     * Retrieves all posts for a given user.
     *
     * @param userId  The ID of the user whose posts are to be retrieved.
     * @return        A list of PostDto objects representing the user's posts.
     */
    public List<PostDto> getPostsForUser(String userId) {
        List<PostEntity> postEntities = postRepository.findAllPostsForUser(userId);
        return postEntities.stream()
            .map(postEntity -> addIsLikedAndIsPinnedToPostDto(postEntity, userId))
            .toList();
    }

    /**
     * Retrieves a post by its ID for a given user.
     *
     * @param postId   The ID of the post.
     * @param userId   The ID of the user requesting the post.
     * @return         A PostDto containing the post's details.
     * @throws ResponseStatusException if the post is not found.
     */
    public PostDto getPostById(String postId, String userId) {
        PostEntity postEntity = postRepository.findPostById(postId);
        return addIsLikedAndIsPinnedToPostDto(postEntity, userId);
    }

    /**
     * Updates a post's details.
     *
     * @param postId   The ID of the post to be updated.
     * @param userId   The ID of the user requesting the update.
     * @param postDto  The data transfer object containing the updated post information.
     * @return         A PostDto containing the updated post's details.
     * @throws ResponseStatusException if the post is not found.
     */
    public PostDto updatePost(String postId, String userId, PostDto postDto) {
        PostEntity postEntity = postRepository.findPostById(postId);
        if (postEntity == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found");
        postEntity = postMapper.toPostEntity(userId, postDto);
        postRepository.savePost(postEntity);
        return addIsLikedAndIsPinnedToPostDto(postEntity, userId);
    }

    /**
     * Deletes a post by its ID for a given user.
     *
     * @param postId   The ID of the post to be deleted.
     * @param userId   The ID of the user requesting the deletion.
     */
    public void deletePost(String postId, String userId) {
        unpinPost(postId, userId);
        postRepository.deletePostById(postId, userId);
        postRepository.deleteLikesForPost(postId);
        dailyPostRepository.deleteDailyPostById(postId, userId);
        commentRepository.deleteAllCommentsForPost(postId, userId);
    }

    /**
     * Finds all users who liked a specific post.
     *
     * @param postId  The ID of the post.
     * @return        A list of user IDs who liked the post.
     */
    public List<String> findAllUsersWhoLikedPost(String postId) {
        return postRepository.findAllUsersWhoLikedPost(postId);
    }

    /**
     * Likes a post for a given user.
     *
     * @param postId   The ID of the post to be liked.
     * @param userId   The ID of the user liking the post.
     * @throws ResponseStatusException if the post is not found.
     */
    public void likePost(String postId, String userId) {
        PostEntity postEntity = postRepository.findPostById(postId);
        if (postEntity == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found");
        LikeEntity likeEntity = postMapper.toLikeEntity(postId, userId);
        postRepository.likePost(likeEntity);
    }

    /**
     * Unlikes a post for a given user.
     *
     * @param postId   The ID of the post to be unliked.
     * @param userId   The ID of the user unliking the post.
     * @throws ResponseStatusException if the post is not found.
     */
    public void unlikePost(String postId, String userId) {
        PostEntity postEntity = postRepository.findPostById(postId);
        if (postEntity == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found");
        LikeEntity likeEntity = postMapper.toLikeEntity(postId, userId);
        postRepository.unlikePost(likeEntity);
    }

    /**
     * Retrieves the daily post for a specific user.
     *
     * @param userId           The ID of the user whose daily post is to be retrieved.
     * @param requestingUserId The ID of the user making the request.
     * @return                 A PostDto containing the daily post's details.
     * @throws ResponseStatusException if the daily post is not found.
     */
    public PostDto getDailyPostForUser(String userId, String requestingUserId) {
        String postId = dailyPostRepository.findDailyPostForUser(userId);
        PostEntity postEntity = postRepository.findPostById(postId);
        return addIsLikedAndIsPinnedToPostDto(postEntity, requestingUserId);
    }

    /**
     * Retrieves all pinned posts for a user.
     *
     * @param userId  The ID of the user whose pinned posts are to be retrieved.
     * @return        A list of PostDto objects representing the pinned posts.
     */
    public List<PostDto> getPinnedPostsByUserId(String userId) {
        List<String> pinnedPostEntities = pinnedPostRepository.findPinnedPostIdsForUser(userId);
        return pinnedPostEntities.stream()
            .map(postRepository::findPostById)
            .map(postEntity -> addIsLikedAndIsPinnedToPostDto(postEntity, userId))
            .toList();
    }

    /**
     * Pins a post for a given user.
     *
     * @param postId   The ID of the post to be pinned.
     * @param userId   The ID of the user pinning the post.
     * @return         A PostDto containing the pinned post's details.
     * @throws NoSuchElementException if the post does not exist.
     */
    public PostDto pinPost(String postId, String userId) {
        PostEntity postEntity = postRepository.findPostById(postId);
        if (postEntity == null) throw new NoSuchElementException("Post with id " + postId + " does not exist");
        PinnedPostEntity pinnedPostEntity = new PinnedPostEntity(userId, postEntity.getPostId());
        pinnedPostRepository.savePinnedPost(pinnedPostEntity);
        return addIsLikedAndIsPinnedToPostDto(postEntity, userId);
    }

    /**
     * Unpins a post for a given user.
     *
     * @param postId   The ID of the post to be unpinned.
     * @param userId   The ID of the user unpinning the post.
     */
    public void unpinPost(String postId, String userId) {
        PinnedPostEntity pinnedPostEntity = new  PinnedPostEntity(userId, postId);
        pinnedPostRepository.deleteDailyPostById(pinnedPostEntity);
    }
}
