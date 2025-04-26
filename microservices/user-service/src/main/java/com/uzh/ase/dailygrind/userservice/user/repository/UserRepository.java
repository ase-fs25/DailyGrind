package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DynamoDbTable<UserEntity> userTable;

    public UserEntity findUserById(String userId) {
        return userTable.getItem(r -> r.key(k -> k.partitionValue("USER#" + userId)));
    }

    public List<UserEntity> findAllUserEntities() {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder()
                        .sortValue("INFO")
                        .build()
        );
        return userTable.query(r -> r.queryConditional(queryConditional))
                .items()
                .stream()
                .toList();
    }

    public void saveUser(UserEntity userEntity) {
        userTable.putItem(userEntity);
    }

    public void updateUser(UserEntity userEntity) {
        userTable.updateItem(userEntity);
    }

    public List<String> findAllFollowers(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder()
                        .partitionValue("USER#" + userId + "#FOLLOWER")
                        .build()
        );
        return userTable.query(r -> r.queryConditional(queryConditional))
                .items()
                .stream()
                .map(UserEntity::getSk)
                .map(sk -> sk.split("#")[1])
                .toList();
    }

    public List<String> findAllFollowing(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder()
                        .partitionValue("USER#" + userId + "#FOLLOWING")
                        .build()
        );
        return userTable.query(r -> r.queryConditional(queryConditional))
                .items()
                .stream()
                .map(UserEntity::getSk)
                .map(sk -> sk.split("#")[1])
                .toList();
    }

}
