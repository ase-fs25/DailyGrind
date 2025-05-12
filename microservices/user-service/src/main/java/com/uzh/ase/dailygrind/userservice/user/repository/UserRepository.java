package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DynamoDbTable<UserEntity> userTable;

    public UserEntity findUserById(String userId) {
        return userTable.getItem(r -> r.key(k -> k
            .partitionValue("USER#" + userId)
            .sortValue("INFO")
        ));
    }

    public List<UserEntity> findAllUserEntities() {
        return userTable.scan()
            .items()
            .stream()
            .filter(user -> user.getPk().startsWith("USER#") && user.getSk().equals("INFO"))
            .toList();
    }

    public void saveUser(UserEntity userEntity) {
        userTable.putItem(userEntity);
    }

    public void updateUser(UserEntity userEntity) {
        userTable.updateItem(userEntity);
    }

    public void deleteUser(UserEntity userEntity) {
        userTable.deleteItem(userEntity);
    }
}
