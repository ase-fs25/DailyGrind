package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.PinnedPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

/**
 * Repository for managing pinned post entities in DynamoDB.
 * <p>
 * This class provides methods for performing CRUD operations on pinned post data in the DynamoDB table,
 * such as saving, deleting, and querying pinned posts for a specific user.
 */
@Repository
@RequiredArgsConstructor
public class PinnedPostRepository {

    private final DynamoDbTable<PinnedPostEntity> pinnedPostTable;

    /**
     * Saves a pinned post entity to DynamoDB.
     * <p>
     * This method stores a new pinned post or updates an existing one.
     *
     * @param pinnedPost the pinned post entity to save
     */
    public void savePinnedPost(PinnedPostEntity pinnedPost) {
        pinnedPostTable.putItem(pinnedPost);
    }

    /**
     * Retrieves the list of pinned post IDs for a specific user.
     * <p>
     * This method queries the DynamoDB table to find all pinned posts for the user by matching the user's partition key (PK).
     * It returns a list of post IDs associated with the user's pinned posts.
     *
     * @param userId the ID of the user to retrieve the pinned posts for
     * @return a list of pinned post IDs for the user
     */
    public List<String> findPinnedPostIdsForUser(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(PinnedPostEntity.generatePK(userId)).build()
        );

        return pinnedPostTable.query(queryConditional)
            .items()
            .stream()
            .map(PinnedPostEntity::getId)
            .toList();
    }

    /**
     * Deletes a specific pinned post from DynamoDB.
     * <p>
     * This method deletes the pinned post entity from the DynamoDB table.
     *
     * @param pinnedPost the pinned post entity to delete
     */
    public void deleteDailyPostById(PinnedPostEntity pinnedPost) {
        pinnedPostTable.deleteItem(pinnedPost);
    }
}
