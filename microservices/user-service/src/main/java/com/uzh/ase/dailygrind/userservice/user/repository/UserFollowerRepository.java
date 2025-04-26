package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserFollowerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserFollowerRepository {

    private final DynamoDbTable<UserFollowerEntity> userFollowerTable;

    public Boolean isFollowed(String followingId, String followerId) {
        Key key = Key.builder()
                .partitionValue("USER#" + followingId + "#FOLLOWER")
                .sortValue("USER#" + followerId)
                .build();
        return userFollowerTable.getItem(key) != null;
    }

    public List<String> findAllFollowers(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder()
                        .partitionValue("USER#" + userId + "#FOLLOWER")
                        .build()
        );
        return userFollowerTable.query(r -> r.queryConditional(queryConditional))
                .items()
                .stream()
                .map(UserFollowerEntity::getId)
                .toList();
    }

    public List<String> findAllFollowing(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder()
                        .partitionValue("USER#" + userId + "#FOLLOWING")
                        .build()
        );
        return userFollowerTable.query(r -> r.queryConditional(queryConditional))
                .items()
                .stream()
                .map(UserFollowerEntity::getId)
                .toList();
    }

    public void followUser(String toFollowId, String userId) {
        UserFollowerEntity userFollowingEntity = UserFollowerEntity.builder()
                .pk("USER#" + toFollowId + "#FOLLOWER")
                .sk("USER#" + userId)
                .build();
        UserFollowerEntity userFollowerEntity = UserFollowerEntity.builder()
                .pk("USER#" + userId + "#FOLLOWING")
                .sk("USER#" + toFollowId)
                .build();
        userFollowerTable.putItem(userFollowingEntity);
        userFollowerTable.putItem(userFollowerEntity);
    }

    public void unfollowUser(String toUnfollowId, String userId) {
        userFollowerTable.deleteItem(r -> r.key(k -> k.partitionValue("USER#" + toUnfollowId + "#FOLLOWER").sortValue(userId)));
        userFollowerTable.deleteItem(r -> r.key(k -> k.partitionValue("USER#" + userId + "#FOLLOWING").sortValue(toUnfollowId)));
    }

}
