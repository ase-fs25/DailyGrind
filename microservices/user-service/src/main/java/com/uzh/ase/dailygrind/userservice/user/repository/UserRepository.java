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

    private final DynamoDbTable<UserEntity> userTable;

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
        userTable.putItem(user);
        userJobs.forEach(jobTable::putItem);
        userEducations.forEach(educationTable::putItem);
        return user;
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId the ID of the user to find
     * @return the user entity if found, null otherwise
     */
    public UserEntity findUserById(String userId) {
        return userTable.getItem(r -> r.key(k -> k.partitionValue(UserEntity.ID_NAME + "#" + userId)));
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId the ID of the user to find
     * @return the user entity if found, null otherwise
     */
    public UserDto findUserDetailsById(String userId) {

        UserEntity user = userTable.scan().items().stream().filter(item -> item.getSk().equals(UserEntity.ID_NAME + "#" + userId) && !(item.getPk().endsWith("#FOLLOWER") || (item.getPk().endsWith("#FOLLOWING")))).findFirst().orElse(null);
        if (user == null) return null;
        List<UserJobEntity> userJobs = jobTable.scan().items().stream().filter(item -> item.getPk().equals(UserEntity.ID_NAME + "#" + userId + "#" + UserJobEntity.ID_NAME)).toList();
        List<UserEducationEntity> userEducations = educationTable.scan().items().stream().filter(item -> item.getPk().equals(UserEntity.ID_NAME + "#" + userId + "#" + UserEducationEntity.ID_NAME)).toList();

        System.out.println(userJobs.size());

        List<UserJobEntity> jobs = userJobs.stream()
                .filter(job -> job.getPk().startsWith(user.getPk()))
                .toList();
        List<UserEducationEntity> educations = userEducations.stream()
                .filter(education -> education.getPk().startsWith(user.getPk()))
                .toList();

        return userMapper.toUserDto(user, jobs, educations);

    }

    /**
     * Finds all user details.
     *
     * @return a list of user DTOs
     */
    public List<UserDto> findAllUserDetails() {

       List<UserEntity> users = userTable.scan().items().stream().filter(item -> item.getSk().startsWith(UserEntity.ID_NAME) && !(item.getPk().endsWith("#FOLLOWER") || (item.getPk().endsWith("#FOLLOWING")))).toList();
       List<UserJobEntity> userJobs = jobTable.scan().items().stream().filter(item -> item.getSk().startsWith(UserJobEntity.ID_NAME)).toList();
       List<UserEducationEntity> userEducations = educationTable.scan().items().stream().filter(item -> item.getSk().startsWith(UserEducationEntity.ID_NAME)).toList();

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

    /**
     * Follows a user.
     *
     * @param toFollow the user to follow
     * @param follower the user who is following
     */
    public void followUser(String toFollow, String follower) {
        UserEntity toFollowEntity = UserEntity.builder()
                .pk(UserEntity.ID_NAME + "#" + toFollow + "#FOLLOWER")
                .sk(UserEntity.ID_NAME + "#" + follower).build();
        userTable.putItem(toFollowEntity);
        UserEntity followerEntity = UserEntity.builder()
                .pk(UserEntity.ID_NAME + "#" + follower + "#FOLLOWING")
                .sk(UserEntity.ID_NAME + "#" + toFollow ).build();
        userTable.putItem(followerEntity);
    }

    /**
     * Unfollows a user.
     *
     * @param toUnfollow the user to unfollow
     * @param follower   the user who is unfollowing
     */
    public void unfollowUser(String toUnfollow, String follower) {
        UserEntity toUnfollowEntity = UserEntity.builder()
                .pk(UserEntity.ID_NAME + "#" + toUnfollow + "#FOLLOWER")
                .sk(UserEntity.ID_NAME + "#" + follower).build();
        userTable.deleteItem(toUnfollowEntity);
        UserEntity followerEntity = UserEntity.builder()
                .pk(UserEntity.ID_NAME + "#" + follower + "#FOLLOWING")
                .sk(UserEntity.ID_NAME + "#" + toUnfollow).build();
        userTable.deleteItem(followerEntity);
    }

    /**
     * Finds all users that a user is following.
     *
     * @param userId the ID of the user
     * @return a list of user IDs that the user is following
     */
    public List<String> findAllFollowing(String userId) {
        return userTable.scan().items().stream()
                .filter(item -> item.getPk().equals(UserEntity.ID_NAME + "+" + userId + "#FOLLOWING"))
                .map(userEntity -> userEntity.getSk().split("#")[1])
                .toList();
    }

    /**
     * Finds all followers of a user
     *
     * @param userId the ID of the user
     * @return a list of user IDs that are following the user
     */
    public List<String> findAllFollowers(String userId) {
        return userTable.scan().items().stream()
                .filter(item -> item.getSk().startsWith(UserEntity.ID_NAME + "#" + userId + "#FOLLOWER"))
                .map(userEntity -> userEntity.getSk().split("#")[1])
                .toList();
    }

}
