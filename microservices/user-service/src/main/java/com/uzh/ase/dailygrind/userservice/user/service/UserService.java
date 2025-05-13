package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.*;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFriendRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.UserRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.sns.UserEventPublisher;
import com.uzh.ase.dailygrind.userservice.user.sns.events.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for managing user-related operations,
 * including retrieval, creation, updates, deletion,
 * and enrichment with job and education data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
        log.info("Fetching all user infos for requester with ID: {}", requesterId);
        List<UserEntity> userEntities = userRepository.findAllUserEntities();
        List<String> followingIds = userFriendRepository.findAllFriends(requesterId);

        List<UserInfoDto> result = userEntities.stream()
            .map(userEntity -> userMapper.toUserInfoDto(userEntity, followingIds.contains(requesterId)))
            .filter(userInfoDto -> !userInfoDto.userId().equals(requesterId))
            .toList();

        log.info("Found {} user infos for requester {}", result.size(), requesterId);
        return result;
    }

    /**
     * Gets user info for a specific user, including friendship status.
     *
     * @param followingId ID of the user to retrieve
     * @param requesterId ID of the requesting user
     * @return {@link UserInfoDto} or null if user not found
     */
    public UserInfoDto getUserInfo(String followingId, String requesterId) {
        log.info("Fetching user info for user with ID: {} for requester with ID: {}", followingId, requesterId);
        UserEntity userEntity = userRepository.findUserById(followingId);
        if (userEntity == null) {
            log.warn("User with ID: {} not found", followingId);
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
        log.info("Fetching detailed info for user with ID: {} for requester with ID: {}", userId, requesterId);
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity == null) {
            log.warn("User with ID: {} not found", userId);
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
        log.info("Fetching user info for userId: {} requested by requesterId: {}", userId, requesterId);  // Log method entry

        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity == null) {
            log.warn("User not found for userId: {}", userId);  // Log if the user is not found
            return null;
        }

        boolean isFollowing = false;
        if (!userId.equals(requesterId)) {
            isFollowing = userFriendRepository.isFriend(userId, requesterId);
            log.info("UserId: {} is {}following the requesterId: {}", userId, isFollowing ? "" : "not ", requesterId);  // Log the friendship status
        }

        UserInfoDto userInfoDto = userMapper.toUserInfoDto(userEntity, isFollowing);
        log.info("Successfully fetched user info for userId: {}", userId);  // Log successful retrieval

        return userInfoDto;
    }

    /**
     * Creates a new user and publishes a USER_CREATED event.
     *
     * @param createUserDto DTO containing user data
     * @param userId        ID to associate with the new user
     * @return created {@link UserInfoDto}
     */
    public UserInfoDto createUser(UserCreateDto createUserDto, String userId) {
        log.info("Creating a new user with ID: {}", userId);
        UserEntity userEntity = userMapper.toUserEntity(createUserDto, userId);
        userRepository.saveUser(userEntity);
        userEventPublisher.publishUserEvent(EventType.USER_CREATED, userMapper.toUserDataEvent(userEntity));
        log.info("User with ID: {} created successfully", userId);
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
        log.info("Updating user with ID: {}", name);
        UserEntity userEntity = userMapper.toUserEntity(updateUserDto, name);
        userRepository.updateUser(userEntity);
        userEventPublisher.publishUserEvent(EventType.USER_UPDATED, userMapper.toUserDataEvent(userEntity));
        log.info("User with ID: {} updated successfully", name);
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
        log.info("Searching for users by name: '{}' requested by requesterId: {}", name, requesterId);  // Log method entry

        List<UserInfoDto> matchingUsers = userRepository.findAllUserEntities().stream()
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

        if (matchingUsers.isEmpty()) {
            log.warn("No users found matching the search query: '{}' for requesterId: {}", name, requesterId);  // Log if no results
        } else {
            log.info("Found {} users matching the search query: '{}'", matchingUsers.size(), name);  // Log the number of matching users
        }

        return matchingUsers;
    }

    /**
     * Deletes a user along with associated job and education data.
     * Also publishes a USER_DELETED event.
     *
     * @param userId ID of the user to delete
     */
    public void deleteUser(String userId) {
        log.info("Deleting user with ID: {}", userId);
        UserEntity userEntity = userRepository.findUserById(userId);
        if (userEntity != null) {
            userRepository.deleteUser(userEntity);
            userJobService.deleteJobsForUser(userId);
            userEducationService.deleteEducationForUser(userId);
            userEventPublisher.publishUserEvent(EventType.USER_DELETED, userMapper.toUserDataEvent(userEntity));
            log.info("User with ID: {} and associated data deleted successfully", userId);
        } else {
            log.warn("User with ID: {} not found", userId);
        }
    }
}
