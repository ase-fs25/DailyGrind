package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DynamoDbTable<UserEntity> userTable;

    private final DynamoDbTable<FriendEntity> friendTable;

    public void addNewUser(UserEntity userEntity) {
        userTable.putItem(userEntity);
    }

    public void updateUser(UserEntity userEntity) {
        userTable.updateItem(userEntity);
    }

    public void deleteUser(String userId) {
        String pk = UserEntity.generatePK(userId);
        Key key = Key.builder()
                .partitionValue(pk)
                .build();
        userTable.deleteItem(key);

        key = Key.builder()
                .partitionValue(FriendEntity.generatePK(userId))
                .build();
        friendTable.deleteItem(key);
    }

    public void addFriend(FriendEntity friendEntity) {
        friendTable.putItem(friendEntity);
    }

    public void removeFriend(FriendEntity friendEntity) {
        friendTable.deleteItem(friendEntity);
    }
}
