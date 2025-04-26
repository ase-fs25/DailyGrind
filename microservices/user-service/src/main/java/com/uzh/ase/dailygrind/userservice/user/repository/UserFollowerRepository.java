package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserFollowerEntity;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
public class UserFollowerRepository {

    DynamoDbTable<UserFollowerEntity> userFollowerTable;

    public Boolean isFollowing(String followingId, String followerId) {
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
                .map(UserFollowerEntity::getSk)
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
                .map(UserFollowerEntity::getSk)
                .toList();
    }

    public void followUser(String followerId, String followingId) {
        UserFollowerEntity userFollowingEntity = UserFollowerEntity.builder()
                .pk("USER#" + followerId + "#FOLLOWING")
                .sk(followingId)
                .build();
        UserFollowerEntity userFollowerEntity = UserFollowerEntity.builder()
                .pk("USER#" + followingId + "#FOLLOWER")
                .sk(followerId)
                .build();
        userFollowerTable.putItem(userFollowingEntity);
        userFollowerTable.putItem(userFollowerEntity);
    }

    public void unfollowUser(String followerId, String followingId) {
        userFollowerTable.deleteItem(r -> r.key(k -> k.partitionValue("USER#" + followerId + "#FOLLOWING").sortValue(followingId)));
        userFollowerTable.deleteItem(r -> r.key(k -> k.partitionValue("USER#" + followingId + "#FOLLOWER").sortValue(followerId)));
    }

}
