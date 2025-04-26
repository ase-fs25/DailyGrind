package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserEducationRepository {

    private final DynamoDbTable<UserEducationEntity> userEducationTable;

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

    public void saveUserEducation(UserEducationEntity userEducationEntity) {
        userEducationTable.putItem(userEducationEntity);
    }

    public void updateUserEducation(UserEducationEntity userEducationEntity) {
        userEducationTable.updateItem(userEducationEntity);
    }

    public void deleteUserEducation(String userId, String educationId) {
        userEducationTable.deleteItem(r -> r.key(k -> k.partitionValue("USER#" + userId + "#EDUCATION").sortValue("EDUCATION#" + educationId)));
    }

}
