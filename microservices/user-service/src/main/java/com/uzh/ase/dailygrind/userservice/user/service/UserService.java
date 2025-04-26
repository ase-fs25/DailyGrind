package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserCreateDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDetailsDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
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

    private final UserMapper userMapper;

    public List<UserInfoDto> getAllUserInfos(String requesterId) {
        List<UserEntity> userEntities = userRepository.findAllUserEntities();
        List<String> followingIds = userEntities.stream()
                .map(UserEntity::getSk)
                .map(sk -> sk.split("#")[1])
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
        List<UserJobDto> userJobDtos = userJobService.getJobsForUser(userId);
        return null;
    }

    public UserInfoDto createUser(UserCreateDto createUserDto, String name) {
        return null;
    }

    public UserInfoDto updateUser(UserCreateDto updateUserDto, String name) {
        return null;
    }
}
