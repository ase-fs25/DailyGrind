package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDto;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DynamoDbTable<UserEntity> table;

    private final DynamoDbTable<UserJobEntity> jobTable;

    private final DynamoDbTable<UserEducationEntity> educationTable;
    private final UserMapper userMapper;

    /**
     * Saves a user entity to the DynamoDB table.
     *
     * @param user the user entity to save
     * @return the saved user entity
     */
    public UserEntity save(UserEntity user, List<UserJobEntity> userJobs, List<UserEducationEntity> userEducations) {
        table.putItem(user);
        userJobs.forEach(jobTable::putItem);
        userEducations.forEach(educationTable::putItem);
        return user;
    }

    public List<UserDto> findAllUserDetails() {

       List<UserEntity> users = table.scan().items().stream().filter(item -> item.getSk().startsWith("USER#")).toList();
       List<UserJobEntity> userJobs = jobTable.scan().items().stream().filter(item -> item.getSk().startsWith("JOB#")).toList();
       List<UserEducationEntity> userEducations = educationTable.scan().items().stream().filter(item -> item.getSk().startsWith("EDUCATION#")).toList();

       List<UserDto> userDtos = new ArrayList<>();

        for (UserEntity user : users) {
            List<UserJobEntity> jobs = userJobs.stream()
                    .filter(job -> job.getPk().equals(user.getPk()))
                    .toList();
            List<UserEducationEntity> educations = userEducations.stream()
                    .filter(education -> education.getPk().equals(user.getPk()))
                    .toList();

            userDtos.add(userMapper.toUserDto(user, jobs, educations));
        }

        return userDtos;

    }

}
