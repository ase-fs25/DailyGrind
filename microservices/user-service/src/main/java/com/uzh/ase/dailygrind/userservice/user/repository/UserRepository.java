package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

/**
 * Repository class for managing user profile data in DynamoDB.
 * Provides methods for basic CRUD operations on {@link UserEntity}.
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DynamoDbTable<UserEntity> userTable;

    /**
     * Retrieves a user entity by their user ID.
     *
     * @param userId the unique identifier of the user
     * @return the {@link UserEntity} if found, otherwise null
     */
    public UserEntity findUserById(String userId) {
        return userTable.getItem(r -> r.key(k -> k
            .partitionValue("USER#" + userId)
            .sortValue("INFO")
        ));
    }

    /**
     * Retrieves all user entities in the table.
     * Filters entries to include only primary user info items.
     *
     * @return a list of {@link UserEntity} instances
     */
    public List<UserEntity> findAllUserEntities() {
        return userTable.scan()
            .items()
            .stream()
            .filter(user -> user.getPk().startsWith("USER#") && user.getSk().equals("INFO"))
            .toList();
    }

    /**
     * Saves a new user entity to the table.
     *
     * @param userEntity the user entity to save
     */
    public void saveUser(UserEntity userEntity) {
        userTable.putItem(userEntity);
    }

    /**
     * Updates an existing user entity in the table.
     *
     * @param userEntity the user entity to update
     */
    public void updateUser(UserEntity userEntity) {
        userTable.updateItem(userEntity);
    }

    /**
     * Deletes a user entity from the table.
     *
     * @param userEntity the user entity to delete
     */
    public void deleteUser(UserEntity userEntity) {
        userTable.deleteItem(userEntity);
    }
}
