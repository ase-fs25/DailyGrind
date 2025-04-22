package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDto;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

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

    public UserDto findUserDetailsById(String userId) {

        UserEntity user = table.scan().items().stream().filter(item -> item.getSk().equals("USER#" + userId)).findFirst().orElse(null);
        if (user == null) return null;
        List<UserJobEntity> userJobs = jobTable.scan().items().stream().filter(item -> item.getSk().startsWith("JOB#")).toList();
        List<UserEducationEntity> userEducations = educationTable.scan().items().stream().filter(item -> item.getSk().startsWith("EDUCATION#")).toList();

        List<UserJobEntity> jobs = userJobs.stream()
                .filter(job -> job.getPk().startsWith(user.getPk()))
                .toList();
        List<UserEducationEntity> educations = userEducations.stream()
                .filter(education -> education.getPk().startsWith(user.getPk()))
                .toList();

        return userMapper.toUserDto(user, jobs, educations);

    }

    public List<UserDto> findAllUserDetails() {

       List<UserEntity> users = table.scan().items().stream().filter(item -> item.getSk().startsWith("USER#")).toList();
       List<UserJobEntity> userJobs = jobTable.scan().items().stream().filter(item -> item.getSk().startsWith("JOB#")).toList();
       List<UserEducationEntity> userEducations = educationTable.scan().items().stream().filter(item -> item.getSk().startsWith("EDUCATION#")).toList();

       List<UserDto> userDtos = new ArrayList<>();

        for (UserEntity user : users) {
            List<UserJobEntity> jobs = userJobs.stream()
                    .filter(job -> job.getPk().startsWith(user.getPk()))
                    .toList();
            List<UserEducationEntity> educations = userEducations.stream()
                    .filter(education -> education.getPk().startsWith(user.getPk()))
                    .toList();

            userDtos.add(userMapper.toUserDto(user, jobs, educations));
        }

        return userDtos;

    }

    public void followUser(String toFollow, String follower) {
        UserEntity toFollowEntity = UserEntity.builder()
                .pk("USER#" + toFollow)
                .sk("FOLLOWER#" + follower).build();
        table.putItem(toFollowEntity);
        UserEntity followerEntity = UserEntity.builder()
                .pk("USER#" + follower)
                .sk("FOLLOWING#" + toFollow).build();
        table.putItem(followerEntity);
    }

    public void unfollowUser(String toUnfollow, String follower) {
        UserEntity toUnfollowEntity = UserEntity.builder()
                .pk("USER#" + toUnfollow)
                .sk("FOLLOWER#" + follower).build();
        table.deleteItem(toUnfollowEntity);
        UserEntity followerEntity = UserEntity.builder()
                .pk("USER#" + follower)
                .sk("FOLLOWING#" + toUnfollow).build();
        table.deleteItem(followerEntity);
    }
}
