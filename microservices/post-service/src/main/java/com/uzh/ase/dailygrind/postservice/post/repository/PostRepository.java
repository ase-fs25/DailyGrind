package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.LikeEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

/**
 * Repository for managing post and like entities in DynamoDB.
 * <p>
 * This class provides methods for performing CRUD operations on post and like data in the DynamoDB tables,
 * such as saving, deleting, querying posts and likes, and updating like counts.
 */
@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final DynamoDbTable<PostEntity> postTable;

    private final DynamoDbTable<LikeEntity> likeTable;

    /**
     * Saves a post entity to DynamoDB.
     * <p>
     * This method stores a new post or updates an existing one.
     *
     * @param post the post entity to save
     */
    public void savePost(PostEntity post) {
        postTable.putItem(post);
    }

    /**
     * Retrieves all posts for a specific user.
     * <p>
     * This method queries the DynamoDB table to find all posts for the user by matching the user's partition key (PK).
     *
     * @param userId the ID of the user to retrieve the posts for
     * @return a list of post entities for the user
     */
    public List<PostEntity> findAllPostsForUser(String userId) {
        QueryConditional queryConditional = QueryConditional
            .keyEqualTo(Key.builder()
                .partitionValue(PostEntity.generatePK(userId))
                .build());

        return postTable.query(queryConditional).items().stream().toList();
    }

    /**
     * Retrieves a post entity by its ID.
     * <p>
     * This method scans the DynamoDB table and filters for the post with the provided post ID.
     *
     * @param postId the ID of the post to retrieve
     * @return the post entity if found, otherwise null
     */
    public PostEntity findPostById(String postId) {
        return postTable.scan().items().stream()
            .filter(item -> item.getPk().endsWith(PostEntity.POSTFIX))
            .filter(item -> item.getSk().equals(PostEntity.POSTFIX + "#" + postId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Deletes a specific post by its ID and user ID.
     * <p>
     * This method deletes a post entity from the DynamoDB table based on the provided post ID and user ID.
     *
     * @param postId the ID of the post to delete
     * @param userId the ID of the user who owns the post
     */
    public void deletePostById(String postId, String userId) {
        String pk = PostEntity.generatePK(userId);
        String sk = PostEntity.generateSK(postId);
        postTable.deleteItem(PostEntity.builder().pk(pk).sk(sk).build());
    }

    /**
     * Likes a specific post.
     * <p>
     * This method adds a like entry to the likes table and increments the like count of the corresponding post.
     *
     * @param likeEntity the like entity to add
     */
    public void likePost(LikeEntity likeEntity) {
        likeTable.putItem(likeEntity);

        PostEntity postToLike = findPostById(likeEntity.getPostId());
        postToLike.setLikeCount(postToLike.getLikeCount() + 1);
        postTable.putItem(postToLike);
    }

    /**
     * Unlikes a specific post.
     * <p>
     * This method removes the like entry from the likes table and decrements the like count of the corresponding post.
     *
     * @param likeEntity the like entity to remove
     */
    public void unlikePost(LikeEntity likeEntity) {
        likeTable.deleteItem(likeEntity);

        PostEntity postToUnlike = findPostById(likeEntity.getPostId());
        postToUnlike.setLikeCount(postToUnlike.getLikeCount() - 1);
        postTable.putItem(postToUnlike);
    }

    /**
     * Deletes all likes associated with a specific post.
     * <p>
     * This method queries the likes table to find all like entries for a post and deletes them.
     *
     * @param postId the ID of the post whose likes should be deleted
     */
    public void deleteLikesForPost(String postId) {
        QueryConditional queryConditional = QueryConditional
            .keyEqualTo(Key.builder()
                .partitionValue(PostEntity.POSTFIX + "#" + postId + "LIKELIST")
                .build());

        List<PostEntity> likes = postTable.query(queryConditional).items().stream().toList();
        for (PostEntity like : likes) {
            postTable.deleteItem(like);
        }
    }

    /**
     * Deletes all posts for a specific user.
     * <p>
     * This method retrieves all posts for the user and deletes them from the DynamoDB table.
     *
     * @param userId the ID of the user whose posts should be deleted
     */
    public void deleteAllPosts(String userId) {
        List<PostEntity> posts = findAllPostsForUser(userId);
        for (PostEntity post : posts) {
            deletePostById(post.getPostId(), userId);
        }
    }

    /**
     * Deletes all likes for a specific user.
     * <p>
     * This method scans the post table for likes and removes them from the DynamoDB table.
     *
     * @param userId the ID of the user whose likes should be deleted
     */
    public void deleteAllLikes(String userId) {
        List<PostEntity> likes = postTable.scan().items().stream()
            .filter(item -> item.getPk().endsWith(userId))
            .toList();
        for (PostEntity like : likes) {
            LikeEntity likeEntity = LikeEntity.builder()
                .pk(LikeEntity.generatePK(like.getPostId()))
                .sk(LikeEntity.generateSK(userId))
                .build();
            unlikePost(likeEntity);
        }
    }

    /**
     * Retrieves the list of user IDs who have liked a specific post.
     * <p>
     * This method queries the likes table to find all users who have liked a particular post.
     *
     * @param postId the ID of the post whose likers should be retrieved
     * @return a list of user IDs who liked the post
     */
    public List<String> findAllUsersWhoLikedPost(String postId) {
        QueryConditional queryConditional = QueryConditional
            .keyEqualTo(Key.builder()
                .partitionValue(LikeEntity.generatePK(postId))
                .build());

        return likeTable.query(queryConditional).items().stream()
            .map(LikeEntity::getUserId)
            .toList();
    }
}
