package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.DailyPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DailyPostRepository {

    private final DynamoDbTable<DailyPostEntity> dailyPostTable;

    public void saveDailyPost(DailyPostEntity dailyPost) {
        dailyPostTable.putItem(dailyPost);
    }

    public String findDailyPostIdForUserId(String userId) {
        Key key = Key.builder()
                .partitionValue(DailyPostEntity.generatePK(userId))
                .build();
        return dailyPostTable.getItem(key).getId();
    }

    public String findDailyPostForUser(String userId) {
//        Key key = Key.builder()
//                .partitionValue(DailyPostEntity.generatePK(userId))
//                .build();
//        return Optional.ofNullable(dailyPostTable.getItem(key))
//            .map(DailyPostEntity::getId)
//            .orElse(null);
        return dailyPostTable.scan().items().stream()
            .filter(item -> item.getPk().equals(DailyPostEntity.generatePK(userId)))
            .map(DailyPostEntity::getId)
            .findFirst()
            .orElse(null);
    }
}
