package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.DailyPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
@RequiredArgsConstructor
public class DailyPostRepository {

    private final DynamoDbTable<DailyPostEntity> dailyPostTable;

    public void saveDailyPost(DailyPostEntity dailyPost) {
        dailyPostTable.putItem(dailyPost);
    }

    public String findDailyPostForUser(String userId) {
        return dailyPostTable.scan().items().stream()
            .filter(item -> item.getPk().equals(DailyPostEntity.generatePK(userId)))
            .map(DailyPostEntity::getPostId)
            .findFirst()
            .orElse(null);
    }

    public void deleteDailyPostById(String postId, String userId) {
        Key key = Key.builder()
                .partitionValue(DailyPostEntity.generatePK(userId))
                .sortValue(DailyPostEntity.generateSK(postId))
                .build();
        dailyPostTable.deleteItem(key);
    }
}
