package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.*;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFollowerRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.UserRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserJobService userJobService;
    private final UserEducationService userEducationService;
    private final UserFollowerRepository userFollowerRepository;

    private final UserMapper userMapper;

    public List<UserInfoDto> getAllUserInfos(String requesterId) {
        List<UserEntity> userEntities = userRepository.findAllUserEntities();
        List<String> followingIds = userFollowerRepository.findAllFollowing(requesterId);

        return userEntities.stream()
                .map(userEntity -> userMapper.toUserInfoDto(userEntity, followingIds.contains(requesterId)))
                .toList();
    }

    public UserInfoDto getUserInfo(String followingId, String requesterId) {
        UserEntity userEntity = userRepository.findUserById(followingId);
        if (userEntity == null) {
            return null;
        }
        boolean isFollowed = userFollowerRepository.isFollowed(followingId, requesterId);
        return userMapper.toUserInfoDto(userEntity, isFollowed);
    }

    public UserDetailsDto getUserDetailsById(String userId, String requesterId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity == null) {
            return null;
        }
        List<String> followingIds = userRepository.findAllFollowing(requesterId);
        boolean isFollowing = followingIds.contains(userId);
        UserInfoDto userInfoDto = userMapper.toUserInfoDto(userEntity, isFollowing);
        List<UserJobDto> userJobDtos = userJobService.getJobsForUser(userId);
        List<UserEducationDto> userEducationDtos = userEducationService.getEducationForUser(userId);

        return new UserDetailsDto(userInfoDto, userJobDtos, userEducationDtos);
    }

    public UserInfoDto getUserInfoById(String userId, String requesterId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity == null) {
            return null;
        }
        boolean isFollowing = false;
        if (!userId.equals(requesterId)) {
            isFollowing = userFollowerRepository.isFollowed(userId, requesterId);
        }
        return userMapper.toUserInfoDto(userEntity, isFollowing);
    }

    public UserInfoDto createUser(UserCreateDto createUserDto, String userId) {
        UserEntity userEntity = userMapper.toUserEntity(createUserDto, userId);
        userRepository.saveUser(userEntity);
        return userMapper.toUserInfoDto(userEntity, false);
    }

    public UserInfoDto updateUser(UserCreateDto updateUserDto, String name) {
        UserEntity userEntity = userMapper.toUserEntity(updateUserDto, name);
        userRepository.updateUser(userEntity);
        return userMapper.toUserInfoDto(userEntity, false);
    }

    public void increaseFollowerCount(String userId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity != null) {
            userEntity.setNumFollowers(userEntity.getNumFollowers() + 1);
            userRepository.updateUser(userEntity);
        }
    }

    public void increaseFollowingCount(String userId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity != null) {
            userEntity.setNumFollowing(userEntity.getNumFollowing() + 1);
            userRepository.updateUser(userEntity);
        }
    }

    public void decreaseFollowerCount(String userId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity != null) {
            userEntity.setNumFollowers(userEntity.getNumFollowers() - 1);
            userRepository.updateUser(userEntity);
        }
    }

    public void decreaseFollowingCount(String userId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity != null) {
            userEntity.setNumFollowing(userEntity.getNumFollowing() - 1);
            userRepository.updateUser(userEntity);
        }
    }
    public List<UserInfoDto> searchUsersByName(String searchTerm, String requesterId) {
        List<UserEntity> userEntities = userRepository.findAllUserEntities();
        List<String> followingIds = userFollowerRepository.findAllFollowing(requesterId);

        return userEntities.stream()
                .filter(user -> user.getFirstName() != null && user.getFirstName().toLowerCase().startsWith(searchTerm.toLowerCase()))
                .map(user -> userMapper.toUserInfoDto(user, followingIds.contains(user.getId())))
                .toList();
    }

    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity != null) {
            userRepository.deleteUser(userEntity);
            userJobService.deleteJobsForUser(userId);
            userEducationService.deleteEducationForUser(userId);
        }
    }
}
