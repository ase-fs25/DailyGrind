package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserDto getUserDetailsById(String userId) {
        return userRepository.findUserDetailsById(userId);
    }

    public List<UserDto> getAllUserDetails() {
        return userRepository.findAllUserDetails();
    }

    public UserDto createUser(UserDto createUserDto, String userId) {
        UserEntity userEntity = userMapper.toUserEntity(userId, createUserDto);
        List<UserJobEntity> userJobEntities = userMapper.toJobEntities(userId, createUserDto.jobs());
        List<UserEducationEntity> userEducationEntities = userMapper.toEducationEntities(userId, createUserDto.education());

        userRepository.save(userEntity, userJobEntities, userEducationEntities);

        return userMapper.toUserDto(userEntity, userJobEntities, userEducationEntities);
    }

    public void followUser(String toFollow, String follower) {
        userRepository.followUser(toFollow, follower);
    }

    public void unfollowUser(String toUnfollow, String follower) {
        userRepository.unfollowUser(toUnfollow, follower);
    }

    public List<UserDto> getFollowers(String userId) {
        List<String> followerIds = userRepository.findAllFollowers(userId);
        followerIds.forEach(System.out::println);
        return followerIds.stream()
                .map(userRepository::findUserDetailsById)
                .toList();
    }

    public String[] getFollowersIds(String userId) {
        List<String> followerIds = userRepository.findAllFollowers(userId);
        return followerIds.toArray(new String[0]);
    }

    public List<UserDto> getFollowing(String userId) {
        List<String> followingIds = userRepository.findAllFollowing(userId);
        followingIds.forEach(System.out::println);
        return followingIds.stream()
                .map(userRepository::findUserDetailsById)
                .toList();
    }

    public boolean isFollowing(String follower, String following) {
        List<String> followingIds = userRepository.findAllFollowing(follower);
        return followingIds.contains(following);
    }

    public void deleteUserJob(String userId, String jobId) {
        userRepository.deleteUserJob(userId, jobId);
    }

    public void deleteUserEducation(String userId, String educationId) {
        userRepository.deleteUserEducation(userId, educationId);
    }

    public List<UserJobDto> getJobsForUser(String userId) {
        List<UserJobEntity> userJobEntities = userRepository.findJobsForUser(userId);
        return userMapper.toUserJobDtos(userJobEntities);
    }

    public List<UserEducationDto> getEducationForUser(String userId) {
        List<UserEducationEntity> userEducationEntities = userRepository.findEducationForUser(userId);
        return userMapper.toUserEducationDtos(userEducationEntities);
    }
}
