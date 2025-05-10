package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.DailyPostEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PinnedPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PinnedPostRepository {

    private final DynamoDbTable<PinnedPostEntity> pinnedPostTable;

    public void savePinnedPost(PinnedPostEntity pinnedPost) {
        pinnedPostTable.putItem(pinnedPost);
    }

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

    public void deleteDailyPostById(PinnedPostEntity pinnedPost) {
        pinnedPostTable.deleteItem(pinnedPost);
    }
}
