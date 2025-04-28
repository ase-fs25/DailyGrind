package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserJobRepository {

    private final DynamoDbTable<UserJobEntity> userJobTable;

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

    public void saveUserJob(UserJobEntity userJobEntity) {
        userJobTable.putItem(userJobEntity);
    }

    public void updateUserJob(UserJobEntity userJobEntity) {
        userJobTable.updateItem(userJobEntity);
    }

    public void deleteUserJob(String userId, String jobId) {
        userJobTable.deleteItem(r -> r.key(k -> k.partitionValue("USER#" + userId + "#JOB").sortValue("JOB#" + jobId)));
    }

}
