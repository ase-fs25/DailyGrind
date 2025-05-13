package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFriendRepository;
import com.uzh.ase.dailygrind.userservice.user.sns.UserEventPublisher;
import com.uzh.ase.dailygrind.userservice.user.sns.events.EventType;
import com.uzh.ase.dailygrind.userservice.user.sns.events.FriendshipEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service responsible for managing friend relationships between users.
 * Handles sending, accepting, declining, and canceling friend requests, as well as listing friendships.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserFriendService {

    private final UserFriendRepository userFriendRepository;
    private final UserService userService;
    private final UserEventPublisher userEventPublisher;

    /**
     * Sends a friend request from one user to another.
     *
     * @param senderId   ID of the user sending the request
     * @param receiverId ID of the user receiving the request
     * @throws ResponseStatusException if the receiver doesn't exist, they're already friends,
     *                                 or a request already exists
     */
    public void sendFriendRequest(String senderId, String receiverId) {
        log.info("Attempting to send friend request from user {} to user {}", senderId, receiverId);

        if (userService.getUserInfoById(receiverId, senderId) == null) {
            log.error("Receiver with ID {} not found", receiverId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver not found.");
        }

        if (userFriendRepository.isFriend(senderId, receiverId)) {
            log.error("Users {} and {} are already friends", senderId, receiverId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already friends.");
        }

        if (userFriendRepository.existsPendingRequest(senderId, receiverId)) {
            log.error("Friend request already exists between user {} and user {}", senderId, receiverId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already exists.");
        }

        userFriendRepository.createFriendRequest(senderId, receiverId);
        log.info("Friend request sent from user {} to user {}", senderId, receiverId);
    }

    /**
     * Accepts an incoming friend request and creates a friendship.
     *
     * @param requestingUserId ID of the user accepting the request
     * @param senderId         ID of the user who sent the friend request
     * @throws ResponseStatusException if the friend request doesn't exist
     */
    public void acceptFriendRequest(String requestingUserId, String senderId) {
        log.info("User {} is accepting friend request from user {}", requestingUserId, senderId);

        if (!userFriendRepository.existsPendingRequest(senderId, requestingUserId)) {
            log.error("Friend request does not exist between user {} and user {}", senderId, requestingUserId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request does not exist.");
        }

        userFriendRepository.acceptFriendRequest(requestingUserId, senderId);
        FriendshipEvent friendshipEvent = new FriendshipEvent(senderId, requestingUserId);
        userEventPublisher.publishFriendshipEvent(EventType.FRIENDSHIP_CREATED, friendshipEvent);

        log.info("Friendship created between user {} and user {}", senderId, requestingUserId);
    }

    /**
     * Declines an incoming friend request.
     *
     * @param userId   ID of the user declining the request
     * @param friendId ID of the user who sent the request
     * @throws ResponseStatusException if the friend request doesn't exist
     */
    public void declineFriendRequest(String userId, String friendId) {
        log.info("User {} is declining friend request from user {}", userId, friendId);

        if (!userFriendRepository.existsPendingRequest(friendId, userId)) {
            log.error("Friend request does not exist between user {} and user {}", friendId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request does not exist.");
        }

        userFriendRepository.deleteFriendship(userId, friendId);
        log.info("Friend request declined and friendship removed between user {} and user {}", userId, friendId);
    }

    /**
     * Cancels an outgoing friend request.
     *
     * @param userId   ID of the user canceling the request
     * @param friendId ID of the target user
     * @throws ResponseStatusException if the friend request doesn't exist
     */
    public void cancelFriendRequest(String userId, String friendId) {
        log.info("User {} is canceling friend request to user {}", userId, friendId);

        if (!userFriendRepository.existsPendingRequest(friendId, userId)) {
            log.error("Friend request does not exist between user {} and user {}", friendId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request does not exist.");
        }

        userFriendRepository.deleteFriendship(userId, friendId);
        log.info("Friend request canceled between user {} and user {}", userId, friendId);
    }

    /**
     * Retrieves the list of friends for a given user.
     *
     * @param userId            ID of the user whose friends are to be fetched
     * @param requestingUserId  ID of the user making the request (used for visibility logic)
     * @return a list of {@link UserInfoDto} representing the user's friends
     */
    public List<UserInfoDto> getFriends(String userId, String requestingUserId) {
        log.info("Fetching friends list for user {}", userId);
        List<String> friendIds = userFriendRepository.findFriends(userId);
        List<UserInfoDto> friends = friendIds.stream()
            .map(id -> userService.getUserInfo(id, requestingUserId))
            .toList();
        log.info("Found {} friends for user {}", friends.size(), userId);
        return friends;
    }

    /**
     * Retrieves a list of incoming (pending) friend requests for a user.
     *
     * @param userId ID of the user
     * @return a list of {@link UserInfoDto} representing users who sent requests
     */
    public List<UserInfoDto> getIncomingFriendRequests(String userId) {
        log.info("Fetching incoming friend requests for user {}", userId);
        List<String> userIds = userFriendRepository.findUserIdsOfIncomingRequests(userId);
        List<UserInfoDto> incomingRequests = userIds.stream()
            .map(id -> userService.getUserInfo(id, userId))
            .toList();
        log.info("Found {} incoming friend requests for user {}", incomingRequests.size(), userId);
        return incomingRequests;
    }

    /**
     * Retrieves a list of outgoing (pending) friend requests for a user.
     *
     * @param userId ID of the user
     * @return a list of {@link UserInfoDto} representing users the request was sent to
     */
    public List<UserInfoDto> getOutgoingFriendRequests(String userId) {
        log.info("Fetching outgoing friend requests for user {}", userId);
        List<String> outgoingIds = userFriendRepository.findUserIdsOfOutgoingRequests(userId);
        List<UserInfoDto> outgoingRequests = outgoingIds.stream()
            .map(id -> userService.getUserInfo(id, userId))
            .toList();
        log.info("Found {} outgoing friend requests for user {}", outgoingRequests.size(), userId);
        return outgoingRequests;
    }

    /**
     * Removes an existing friendship between two users.
     * If the users are not friends, no action is taken.
     *
     * @param userId   ID of the user initiating the removal
     * @param friendId ID of the friend to remove
     */
    public void removeFriend(String userId, String friendId) {
        log.info("Attempting to remove friendship between user {} and user {}", userId, friendId);

        if (!userFriendRepository.isFriend(userId, friendId)) {
            log.info("No friendship exists between user {} and user {}", userId, friendId);
            return;
        }

        userFriendRepository.deleteFriendship(userId, friendId);
        FriendshipEvent friendshipEvent = new FriendshipEvent(userId, friendId);
        userEventPublisher.publishFriendshipEvent(EventType.FRIENDSHIP_DELETED, friendshipEvent);
        log.info("Friendship removed between user {} and user {}", userId, friendId);
    }
}
