package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.UserDto;
import com.uzh.ase.dailygrind.postservice.post.mapper.UserMapper;
import com.uzh.ase.dailygrind.postservice.post.repository.CommentRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.UserRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.FriendshipEvent;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.UserDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer responsible for handling user-related business logic.
 * This includes managing user profiles, adding/removing friends, and handling user data.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * Retrieves a user by their user ID.
     *
     * @param userId  The ID of the user to be retrieved.
     * @return        The UserDto representing the user.
     * @throws IllegalArgumentException if the user is not found.
     */
    public UserDto getUser(String userId) {
        log.info("Retrieving user with ID: {}", userId);
        UserEntity userEntity = userRepository.getUser(userId);
        if (userEntity == null) {
            log.error("User not found with ID: {}", userId);
            throw new IllegalArgumentException("User not found");
        }
        log.debug("User found: {}", userEntity);
        return userMapper.toUserDto(userEntity);
    }

    /**
     * Adds a new user to the system.
     *
     * @param userDataEvent  The event containing user data.
     */
    public void addNewUser(UserDataEvent userDataEvent) {
        log.info("Adding new user with ID: {}", userDataEvent.userId());
        UserEntity userEntity = userMapper.toUserEntity(userDataEvent);
        userRepository.addNewUser(userEntity);
        log.debug("User added: {}", userEntity);
    }

    /**
     * Updates an existing user's data.
     *
     * @param userDataEvent  The event containing updated user data.
     */
    public void updateUser(UserDataEvent userDataEvent) {
        log.info("Updating user with ID: {}", userDataEvent.userId());
        UserEntity userEntity = userMapper.toUserEntity(userDataEvent);
        userRepository.updateUser(userEntity);
        log.debug("User updated: {}", userEntity);
    }

    /**
     * Deletes a user from the system, including their posts, likes, and comments.
     *
     * @param userId  The ID of the user to be deleted.
     */
    public void deleteUser(String userId) {
        log.info("Deleting user with ID: {}", userId);
        userRepository.deleteUser(userId);
        postRepository.deleteAllPosts(userId);
        postRepository.deleteAllLikes(userId);
        commentRepository.deleteAllCommentsForUser(userId);
        log.debug("User and related data (posts, likes, comments) deleted for user ID: {}", userId);
    }

    /**
     * Adds a new friend for a user.
     *
     * @param friendshipEvent  The event containing the friendship data between two users.
     */
    public void addFriend(FriendshipEvent friendshipEvent) {
        log.info("Adding friend for user: {} with friend: {}", friendshipEvent.userAId(), friendshipEvent.userBId());
        FriendEntity friendEntity = userMapper.toFriendEntity(friendshipEvent);
        userRepository.addFriend(friendEntity);

        // Add also the inverse direction (userB -> userA as a friend)
        friendshipEvent = new FriendshipEvent(friendshipEvent.userBId(), friendshipEvent.userAId());
        friendEntity = userMapper.toFriendEntity(friendshipEvent);
        userRepository.addFriend(friendEntity);

        log.debug("Friendship established between user: {} and user: {}", friendshipEvent.userAId(), friendshipEvent.userBId());
    }

    /**
     * Removes a friend for a user.
     *
     * @param friendshipEvent  The event containing the friendship data to be removed.
     */
    public void removeFriend(FriendshipEvent friendshipEvent) {
        log.info("Removing friend for user: {} with friend: {}", friendshipEvent.userAId(), friendshipEvent.userBId());
        FriendEntity friendEntity = userMapper.toFriendEntity(friendshipEvent);
        userRepository.removeFriend(friendEntity);

        // Remove also the inverse direction (userB -> userA as a friend)
        friendshipEvent = new FriendshipEvent(friendshipEvent.userBId(), friendshipEvent.userAId());
        friendEntity = userMapper.toFriendEntity(friendshipEvent);
        userRepository.removeFriend(friendEntity);

        log.debug("Friendship removed between user: {} and user: {}", friendshipEvent.userAId(), friendshipEvent.userBId());
    }

    /**
     * Retrieves a list of friends for a specific user.
     *
     * @param userId  The ID of the user whose friends are to be retrieved.
     * @return        A list of UserDto objects representing the user's friends.
     */
    public List<UserDto> getFriends(String userId) {
        log.info("Retrieving friends for user with ID: {}", userId);
        List<FriendEntity> friendEntities = userRepository.getFriends(userId);
        log.debug("Found {} friends for user: {}", friendEntities.size(), userId);

        List<UserDto> friends = friendEntities.stream()
            // Map each friendEntity to a UserDto
            .map(friendEntity -> userMapper.toUserDto(userRepository.getUser(friendEntity.getFriendId())))
            .toList();

        log.info("Retrieved {} friends for user with ID: {}", friends.size(), userId);
        return friends;
    }
}
