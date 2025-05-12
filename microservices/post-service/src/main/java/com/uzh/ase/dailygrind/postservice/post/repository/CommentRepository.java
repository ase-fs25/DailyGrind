package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

/**
 * Repository for managing comment entities in DynamoDB.
 * <p>
 * This class provides methods for performing CRUD operations on comment data in the DynamoDB table,
 * such as saving, deleting, and querying comments.
 */
@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final DynamoDbTable<CommentEntity> commentTable;

    /**
     * Saves a comment entity to DynamoDB.
     * <p>
     * This method stores a new comment or updates an existing one.
     *
     * @param commentEntity the comment entity to save
     */
    public void saveComment(CommentEntity commentEntity) {
        commentTable.putItem(commentEntity);
    }

    /**
     * Deletes a specific comment from DynamoDB.
     * <p>
     * This method deletes the comment identified by the given post ID, comment ID, and user ID.
     *
     * @param postId    the post ID to which the comment belongs
     * @param commentId the ID of the comment to delete
     * @param userId    the user ID who created the comment
     */
    public void deleteComment(String postId, String commentId, String userId) {
        CommentEntity commentEntity = CommentEntity.builder()
            .pk(CommentEntity.generatePK(userId, postId))
            .sk(CommentEntity.generateSK(commentId))
            .build();
        commentTable.deleteItem(commentEntity);
    }

    /**
     * Retrieves all comments for a given post from DynamoDB.
     * <p>
     * This method scans the comment table and filters comments by post ID.
     *
     * @param postId the ID of the post for which to retrieve comments
     * @return a list of comments for the specified post
     */
    public List<CommentEntity> findAllCommentsForPost(String postId) {
        return commentTable.scan().items().stream()
            .filter(item -> item.getPk().endsWith(postId + "#COMMENT"))
            .toList();
    }

    /**
     * Deletes all comments associated with a given post and user.
     * <p>
     * This method queries for all comments related to the specified post ID and user ID, and deletes them.
     *
     * @param postId the ID of the post to delete comments from
     * @param userId the ID of the user who made the comments
     */
    public void deleteAllCommentsForPost(String postId, String userId) {
        QueryConditional queryConditional = QueryConditional
            .keyEqualTo(Key.builder()
                .partitionValue(CommentEntity.generatePK(userId, postId))
                .build());

        List<CommentEntity> comments = commentTable.query(queryConditional).items().stream().toList();
        for (CommentEntity comment : comments) {
            commentTable.deleteItem(comment);
        }
    }

    /**
     * Deletes all comments made by a specific user from DynamoDB.
     * <p>
     * This method scans the comment table and deletes all comments where the user is the creator.
     *
     * @param userId the ID of the user whose comments should be deleted
     */
    public void deleteAllCommentsForUser(String userId) {
        List<CommentEntity> comments = commentTable.scan().items().stream()
            .filter(item -> item.getPk().endsWith(userId))
            .toList();
        for (CommentEntity comment : comments) {
            deleteComment(comment.getPostId(), comment.getCommentId(), userId);
        }
    }
}
