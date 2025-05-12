package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

/**
 * Repository class for managing user job entries stored in DynamoDB.
 * Handles CRUD operations related to {@link UserJobEntity}.
 */
@Repository
@RequiredArgsConstructor
public class UserJobRepository {

    private final DynamoDbTable<UserJobEntity> userJobTable;

    /**
     * Retrieves all job entries associated with a specific user.
     *
     * @param userId the ID of the user whose job entries are to be fetched
     * @return a list of {@link UserJobEntity} associated with the given user
     */
    public List<UserJobEntity> findAllUserJobs(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue("USER#" + userId + "#JOB")
                .build()
        );

        return userJobTable.query(r -> r.queryConditional(queryConditional))
            .items()
            .stream()
            .toList();
    }

    /**
     * Saves a new job entry for a user.
     *
     * @param userJobEntity the job entity to be saved
     */
    public void saveUserJob(UserJobEntity userJobEntity) {
        userJobTable.putItem(userJobEntity);
    }

    /**
     * Updates an existing job entry for a user.
     *
     * @param userJobEntity the updated job entity
     */
    public void updateUserJob(UserJobEntity userJobEntity) {
        userJobTable.updateItem(userJobEntity);
    }

    /**
     * Deletes a specific job entry for a user.
     *
     * @param userId the ID of the user
     * @param jobId  the ID of the job entry to be deleted
     */
    public void deleteUserJob(String userId, String jobId) {
        userJobTable.deleteItem(r -> r.key(k -> k.partitionValue("USER#" + userId + "#JOB").sortValue("JOB#" + jobId)));
    }

}
