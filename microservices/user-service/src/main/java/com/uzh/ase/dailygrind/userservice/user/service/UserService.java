package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.*;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFriendRepository;
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
    private final UserFriendRepository UserFriendRepository;

    private final UserMapper userMapper;

    public List<UserInfoDto> getAllUserInfos(String requesterId) {
        List<UserEntity> userEntities = userRepository.findAllUserEntities();
        List<String> followingIds = UserFriendRepository.findAllFriends(requesterId);

        return userEntities.stream()
                .map(userEntity -> userMapper.toUserInfoDto(userEntity, followingIds.contains(requesterId)))
                .toList();
    }

    public UserInfoDto getUserInfo(String followingId, String requesterId) {
        UserEntity userEntity = userRepository.findUserById(followingId);
        if (userEntity == null) {
            return null;
        }
        boolean isFriend = UserFriendRepository.isFriend(followingId, requesterId);
        return userMapper.toUserInfoDto(userEntity, isFriend);
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
            isFollowing = UserFriendRepository.isFriend(userId, requesterId);
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


    public List<UserInfoDto> searchUsersByName(String name, String requesterId) {
        return userRepository.findAllUserEntities().stream()
            .filter(user -> user.getFirstName().toLowerCase().startsWith(name.toLowerCase())
                         || user.getLastName().toLowerCase().startsWith(name.toLowerCase()))
            .map(user -> userMapper.toUserInfoDto(user, requesterId.equals(user.getPk()) ? false : true)) // or however you handle isFriend
            .toList();
    }
    
    
}
