package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;

/**
 * Repository for managing user and friend entities in DynamoDB.
 * <p>
 * This class provides methods for performing CRUD operations on user and friend data in the DynamoDB tables,
 * such as adding, updating, deleting users and friends, and retrieving a user's friends.
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DynamoDbTable<UserEntity> userTable;

    private final DynamoDbTable<FriendEntity> friendTable;

    /**
     * Adds a new user to the user table.
     * <p>
     * This method saves a new user entity to the DynamoDB table.
     *
     * @param userEntity the user entity to add
     */
    public void addNewUser(UserEntity userEntity) {
        userTable.putItem(userEntity);
    }

    /**
     * Updates an existing user in the user table.
     * <p>
     * This method updates an existing user entity in the DynamoDB table.
     *
     * @param userEntity the user entity to update
     */
    public void updateUser(UserEntity userEntity) {
        userTable.updateItem(userEntity);
    }

    /**
     * Deletes a user from the user table and their corresponding friend entries.
     * <p>
     * This method deletes a user from the user table and also removes the user from the friend table
     * by deleting their associated friend entries.
     *
     * @param userId the ID of the user to delete
     */
    public void deleteUser(String userId) {
        String pk = UserEntity.generatePK(userId);
        Key key = Key.builder()
            .partitionValue(pk)
            .build();
        userTable.deleteItem(key);

        // Delete the corresponding friend entries
        key = Key.builder()
            .partitionValue(FriendEntity.generatePK(userId))
            .build();
        friendTable.deleteItem(key);
    }

    /**
     * Adds a friend relationship between two users.
     * <p>
     * This method saves a friend entity to the friend table, establishing a friendship between two users.
     *
     * @param friendEntity the friend entity representing the friendship
     */
    public void addFriend(FriendEntity friendEntity) {
        friendTable.putItem(friendEntity);
    }

    /**
     * Removes a friend relationship between two users.
     * <p>
     * This method deletes a friend entity from the friend table, removing the friendship between two users.
     *
     * @param friendEntity the friend entity to remove
     */
    public void removeFriend(FriendEntity friendEntity) {
        friendTable.deleteItem(friendEntity);
    }

    /**
     * Retrieves a list of friends for a specific user.
     * <p>
     * This method queries the friend table to find all friends for the given user by their partition key.
     *
     * @param userId the ID of the user whose friends should be retrieved
     * @return a list of friend entities for the user
     */
    public List<FriendEntity> getFriends(String userId) {
        String pk = FriendEntity.generatePK(userId);
        Key key = Key.builder()
            .partitionValue(pk)
            .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(key))
            .build();

        return friendTable.query(queryRequest)
            .items()
            .stream()
            .toList();
    }

    /**
     * Retrieves the user entity for a specific user (friend).
     * <p>
     * This method queries the user table to find a user by their unique user ID.
     *
     * @param friendId the ID of the friend (user) to retrieve
     * @return the user entity for the friend if found, otherwise null
     */
    public UserEntity getUser(String friendId) {
        Key key = Key.builder()
            .partitionValue(UserEntity.generatePK(friendId))
            .sortValue(UserEntity.generateSK())
            .build();

        return userTable.getItem(key);
    }
}
