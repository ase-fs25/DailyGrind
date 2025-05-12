package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

/**
 * Repository for managing user education entities in DynamoDB.
 * Provides CRUD operations for user education data.
 */
@Repository
@RequiredArgsConstructor
public class UserEducationRepository {

    private final DynamoDbTable<UserEducationEntity> userEducationTable;

    /**
     * Retrieves all education records for a specified user.
     *
     * @param userId the ID of the user whose education records are to be retrieved
     * @return a list of {@link UserEducationEntity} objects representing the user's education records
     */
    public List<UserEducationEntity> findAllUserEducations(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue("USER#" + userId + "#EDUCATION")
                .build()
        );

        return userEducationTable.query(r -> r.queryConditional(queryConditional))
            .items()
            .stream()
            .toList();
    }

    /**
     * Saves a new education record to DynamoDB.
     *
     * @param userEducationEntity the {@link UserEducationEntity} to be saved
     */
    public void saveUserEducation(UserEducationEntity userEducationEntity) {
        userEducationTable.putItem(userEducationEntity);
    }

    /**
     * Updates an existing education record in DynamoDB.
     *
     * @param userEducationEntity the {@link UserEducationEntity} to be updated
     */
    public void updateUserEducation(UserEducationEntity userEducationEntity) {
        userEducationTable.updateItem(userEducationEntity);
    }

    /**
     * Deletes a user's education record from DynamoDB.
     *
     * @param userId      the ID of the user whose education record is to be deleted
     * @param educationId the ID of the education record to be deleted
     */
    public void deleteUserEducation(String userId, String educationId) {
        userEducationTable.deleteItem(r -> r.key(k -> k.partitionValue("USER#" + userId + "#EDUCATION").sortValue("EDUCATION#" + educationId)));
    }

}
