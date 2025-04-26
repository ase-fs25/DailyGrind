package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.DailyPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
@RequiredArgsConstructor
public class DailyPostRepository {

    DynamoDbTable<DailyPostEntity> dailyPostTable;

    public void saveDailyPost(DailyPostEntity dailyPost) {
        dailyPostTable.putItem(dailyPost);
    }

    public String findDailyPostIdForUserId(String userId) {
        Key key = Key.builder()
                .partitionValue(DailyPostEntity.generatePK(userId))
                .build();
        return dailyPostTable.getItem(key).getId();
    }

}
