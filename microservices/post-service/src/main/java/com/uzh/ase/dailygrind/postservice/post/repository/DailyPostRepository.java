package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.DailyPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

/**
 * Repository for managing daily post entities in DynamoDB.
 * <p>
 * This class provides methods for performing CRUD operations on daily post data in the DynamoDB table,
 * such as saving, deleting, and querying daily posts for a specific user.
 */
@Repository
@RequiredArgsConstructor
public class DailyPostRepository {

    private final DynamoDbTable<DailyPostEntity> dailyPostTable;

    /**
     * Saves a daily post entity to DynamoDB.
     * <p>
     * This method stores a new daily post or updates an existing one.
     *
     * @param dailyPost the daily post entity to save
     */
    public void saveDailyPost(DailyPostEntity dailyPost) {
        dailyPostTable.putItem(dailyPost);
    }

    /**
     * Retrieves the post ID for a specific user.
     * <p>
     * This method scans the DynamoDB table and filters daily posts by the user's partition key (PK).
     * It returns the first post ID associated with the user, or null if no post is found.
     *
     * @param userId the ID of the user to retrieve the daily post for
     * @return the post ID for the user, or null if not found
     */
    public String findDailyPostForUser(String userId) {
        return dailyPostTable.scan().items().stream()
            .filter(item -> item.getPk().equals(DailyPostEntity.generatePK(userId)))
            .map(DailyPostEntity::getPostId)
            .findFirst()
            .orElse(null);
    }

    /**
     * Deletes a specific daily post from DynamoDB.
     * <p>
     * This method deletes the daily post identified by the given post ID and user ID.
     *
     * @param postId the ID of the post to delete
     * @param userId the ID of the user who owns the post
     */
    public void deleteDailyPostById(String postId, String userId) {
        Key key = Key.builder()
            .partitionValue(DailyPostEntity.generatePK(userId))
            .sortValue(DailyPostEntity.generateSK(postId))
            .build();
        dailyPostTable.deleteItem(key);
    }
}
