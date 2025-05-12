package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFriendRepository;
import com.uzh.ase.dailygrind.userservice.user.sns.UserEventPublisher;
import com.uzh.ase.dailygrind.userservice.user.sns.events.EventType;
import com.uzh.ase.dailygrind.userservice.user.sns.events.FriendshipEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service responsible for managing friend relationships between users.
 * Handles sending, accepting, declining, and canceling friend requests, as well as listing friendships.
 */
@Service
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
        if (userService.getUserInfoById(receiverId, senderId) == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver not found.");

        if (userFriendRepository.isFriend(senderId, receiverId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already friends.");

        if (userFriendRepository.existsPendingRequest(senderId, receiverId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already exists.");

        userFriendRepository.createFriendRequest(senderId, receiverId);
    }

    /**
     * Accepts an incoming friend request and creates a friendship.
     *
     * @param requestingUserId ID of the user accepting the request
     * @param senderId         ID of the user who sent the friend request
     * @throws ResponseStatusException if the friend request doesn't exist
     */
    public void acceptFriendRequest(String requestingUserId, String senderId) {
        if (!userFriendRepository.existsPendingRequest(senderId, requestingUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request does not exist.");
        }
        userFriendRepository.acceptFriendRequest(requestingUserId, senderId);
        FriendshipEvent friendshipEvent = new FriendshipEvent(senderId, requestingUserId);
        userEventPublisher.publishFriendshipEvent(EventType.FRIENDSHIP_CREATED, friendshipEvent);
    }

    /**
     * Declines an incoming friend request.
     *
     * @param userId   ID of the user declining the request
     * @param friendId ID of the user who sent the request
     * @throws ResponseStatusException if the friend request doesn't exist
     */
    public void declineFriendRequest(String userId, String friendId) {
        if (!userFriendRepository.existsPendingRequest(friendId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request does not exist.");
        }
        userFriendRepository.deleteFriendship(userId, friendId);
    }

    /**
     * Cancels an outgoing friend request.
     *
     * @param userId   ID of the user canceling the request
     * @param friendId ID of the target user
     * @throws ResponseStatusException if the friend request doesn't exist
     */
    public void cancelFriendRequest(String userId, String friendId) {
        if (!userFriendRepository.existsPendingRequest(friendId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request does not exist.");
        }
        userFriendRepository.deleteFriendship(userId, friendId);
    }

    /**
     * Retrieves the list of friends for a given user.
     *
     * @param userId            ID of the user whose friends are to be fetched
     * @param requestingUserId  ID of the user making the request (used for visibility logic)
     * @return a list of {@link UserInfoDto} representing the user's friends
     */
    public List<UserInfoDto> getFriends(String userId, String requestingUserId) {
        List<String> friendIds = userFriendRepository.findFriends(userId);
        return friendIds.stream()
            .map(id -> userService.getUserInfo(id, requestingUserId))
            .toList();
    }

    /**
     * Retrieves a list of incoming (pending) friend requests for a user.
     *
     * @param userId ID of the user
     * @return a list of {@link UserInfoDto} representing users who sent requests
     */
    public List<UserInfoDto> getIncomingFriendRequests(String userId) {
        List<String> userIds = userFriendRepository.findUserIdsOfIncomingRequests(userId);
        return userIds.stream()
            .map(id -> userService.getUserInfo(id, userId))
            .toList();
    }

    /**
     * Retrieves a list of outgoing (pending) friend requests for a user.
     *
     * @param userId ID of the user
     * @return a list of {@link UserInfoDto} representing users the request was sent to
     */
    public List<UserInfoDto> getOutgoingFriendRequests(String userId) {
        List<String> outgoingIds = userFriendRepository.findUserIdsOfOutgoingRequests(userId);
        return outgoingIds.stream()
            .map(id -> userService.getUserInfo(id, userId))
            .toList();
    }

    /**
     * Removes an existing friendship between two users.
     * If the users are not friends, no action is taken.
     *
     * @param userId   ID of the user initiating the removal
     * @param friendId ID of the friend to remove
     */
    public void removeFriend(String userId, String friendId) {
        if (!userFriendRepository.isFriend(userId, friendId)) return;
        userFriendRepository.deleteFriendship(userId, friendId);
        FriendshipEvent friendshipEvent = new FriendshipEvent(userId, friendId);
        userEventPublisher.publishFriendshipEvent(EventType.FRIENDSHIP_DELETED, friendshipEvent);
    }
}
