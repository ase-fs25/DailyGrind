package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.*;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFriendRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.UserRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.sns.UserEventPublisher;
import com.uzh.ase.dailygrind.userservice.user.sns.events.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for managing user-related operations,
 * including retrieval, creation, updates, deletion,
 * and enrichment with job and education data.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserJobService userJobService;
    private final UserEducationService userEducationService;
    private final UserFriendRepository userFriendRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserMapper userMapper;

    /**
     * Retrieves a list of all users except the requester,
     * marking which users the requester follows.
     *
     * @param requesterId ID of the requesting user
     * @return list of {@link UserInfoDto}
     */
    public List<UserInfoDto> getAllUserInfos(String requesterId) {
        List<UserEntity> userEntities = userRepository.findAllUserEntities();
        List<String> followingIds = userFriendRepository.findAllFriends(requesterId);

        return userEntities.stream()
            .map(userEntity -> userMapper.toUserInfoDto(userEntity, followingIds.contains(requesterId)))
            .filter(userInfoDto -> !userInfoDto.userId().equals(requesterId))
            .toList();
    }

    /**
     * Gets user info for a specific user, including friendship status.
     *
     * @param followingId ID of the user to retrieve
     * @param requesterId ID of the requesting user
     * @return {@link UserInfoDto} or null if user not found
     */
    public UserInfoDto getUserInfo(String followingId, String requesterId) {
        UserEntity userEntity = userRepository.findUserById(followingId);
        if (userEntity == null) {
            return null;
        }
        boolean isFriend = userFriendRepository.isFriend(followingId, requesterId);
        return userMapper.toUserInfoDto(userEntity, isFriend);
    }

    /**
     * Retrieves detailed information about a user including jobs and education.
     *
     * @param userId      ID of the user
     * @param requesterId ID of the requesting user
     * @return {@link UserDetailsDto} or null if user not found
     */
    public UserDetailsDto getUserDetailsById(String userId, String requesterId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity == null) {
            return null;
        }
        boolean isFriend = userFriendRepository.isFriend(requesterId, userId);
        UserInfoDto userInfoDto = userMapper.toUserInfoDto(userEntity, isFriend);
        List<UserJobDto> userJobDtos = userJobService.getJobsForUser(userId);
        List<UserEducationDto> userEducationDtos = userEducationService.getEducationForUser(userId);

        return new UserDetailsDto(userInfoDto, userJobDtos, userEducationDtos);
    }

    /**
     * Retrieves basic user info by ID.
     *
     * @param userId      ID of the user
     * @param requesterId ID of the requesting user
     * @return {@link UserInfoDto} or null if user not found
     */
    public UserInfoDto getUserInfoById(String userId, String requesterId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity == null) {
            return null;
        }
        boolean isFollowing = false;
        if (!userId.equals(requesterId)) {
            isFollowing = userFriendRepository.isFriend(userId, requesterId);
        }
        return userMapper.toUserInfoDto(userEntity, isFollowing);
    }

    /**
     * Creates a new user and publishes a USER_CREATED event.
     *
     * @param createUserDto DTO containing user data
     * @param userId        ID to associate with the new user
     * @return created {@link UserInfoDto}
     */
    public UserInfoDto createUser(UserCreateDto createUserDto, String userId) {
        UserEntity userEntity = userMapper.toUserEntity(createUserDto, userId);
        userRepository.saveUser(userEntity);
        userEventPublisher.publishUserEvent(EventType.USER_CREATED, userMapper.toUserDataEvent(userEntity));
        return userMapper.toUserInfoDto(userEntity, false);
    }

    /**
     * Updates an existing user and publishes a USER_UPDATED event.
     *
     * @param updateUserDto updated user data
     * @param name          user ID
     * @return updated {@link UserInfoDto}
     */
    public UserInfoDto updateUser(UserCreateDto updateUserDto, String name) {
        UserEntity userEntity = userMapper.toUserEntity(updateUserDto, name);
        userRepository.updateUser(userEntity);
        userEventPublisher.publishUserEvent(EventType.USER_UPDATED, userMapper.toUserDataEvent(userEntity));
        return userMapper.toUserInfoDto(userEntity, false);
    }

    /**
     * Searches users by name, matching first name, last name,
     * or combinations of both (with and without space).
     *
     * @param name         search query
     * @param requesterId  ID of the requesting user
     * @return list of matching {@link UserInfoDto}
     */
    public List<UserInfoDto> searchUsersByName(String name, String requesterId) {
        return userRepository.findAllUserEntities().stream()
            .filter(user -> {
                String search = name.toLowerCase();
                String firstName = user.getFirstName().toLowerCase();
                String lastName = user.getLastName().toLowerCase();
                String fullNameNoSpace = (firstName + lastName);
                String fullNameSpace = (firstName + " " + lastName);

                return firstName.startsWith(search)
                    || firstName.endsWith(search)
                    || lastName.startsWith(search)
                    || lastName.endsWith(search)
                    || fullNameNoSpace.startsWith(search)
                    || fullNameNoSpace.endsWith(search)
                    || fullNameSpace.startsWith(search)
                    || fullNameSpace.endsWith(search);
            })
            .map(user -> userMapper.toUserInfoDto(user, userFriendRepository.isFriend(user.getId(), requesterId)))
            .toList();
    }

    /**
     * Deletes a user along with associated job and education data.
     * Also publishes a USER_DELETED event.
     *
     * @param userId ID of the user to delete
     */
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity != null) {
            userRepository.deleteUser(userEntity);
            userJobService.deleteJobsForUser(userId);
            userEducationService.deleteEducationForUser(userId);
            userEventPublisher.publishUserEvent(EventType.USER_DELETED, userMapper.toUserDataEvent(userEntity));
        }
    }
}
