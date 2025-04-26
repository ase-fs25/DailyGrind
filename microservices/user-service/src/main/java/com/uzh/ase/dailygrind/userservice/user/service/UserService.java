package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.*;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserJobService userJobService;
    private final UserEducationService userEducationService;

    private final UserMapper userMapper;

    public List<UserInfoDto> getAllUserInfos(String requesterId) {
        List<UserEntity> userEntities = userRepository.findAllUserEntities();
        List<String> followingIds = userEntities.stream()
                .map(UserEntity::getId)
                .toList();

        return userEntities.stream()
                .map(userEntity -> userMapper.toUserInfoDto(userEntity, followingIds.contains(requesterId)))
                .toList();
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
}
