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

@Service
@RequiredArgsConstructor
public class UserFriendService {

    private final UserFriendRepository userFriendRepository;
    private final UserService userService;
    private final UserEventPublisher userEventPublisher;

    public void sendFriendRequest(String senderId, String receiverId) {
        if (userService.getUserInfoById(receiverId, senderId) == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver not found.");

        if (userFriendRepository.isFriend(senderId, receiverId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already friends.");

        if (userFriendRepository.existsPendingRequest(senderId, receiverId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already exists.");

        userFriendRepository.createFriendRequest(senderId, receiverId);
    }

    public void acceptFriendRequest(String requestingUserId, String senderId) {
        if (!userFriendRepository.existsPendingRequest(senderId, requestingUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request does not exist.");
        }
        userFriendRepository.acceptFriendRequest(requestingUserId, senderId);
        FriendshipEvent friendshipEvent = new FriendshipEvent(senderId, requestingUserId);
        userEventPublisher.publishFriendshipEvent(EventType.FRIENDSHIP_CREATED, friendshipEvent);
    }

    public void declineFriendRequest(String userId, String friendId) {
        if (!userFriendRepository.existsPendingRequest(friendId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request does not exist.");
        }
        userFriendRepository.deleteFriendship(userId, friendId);
    }

    public void cancelFriendRequest(String userId, String friendId) {
        if (!userFriendRepository.existsPendingRequest(friendId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request does not exist.");
        }
        userFriendRepository.deleteFriendship(userId, friendId);
    }

    public List<UserInfoDto> getFriends(String userId, String requestingUserId) {
        List<String> friendIds = userFriendRepository.findFriends(userId);
        return friendIds.stream()
                .map(id -> userService.getUserInfo(id, requestingUserId))
                .toList();
    }

    public List<UserInfoDto> getIncomingFriendRequests(String userId) {
        List<String> userIds = userFriendRepository.findUserIdsOfIncomingRequests(userId);
        return userIds.stream()
                .map(id -> userService.getUserInfo(id, userId))
                .toList();
    }

    public List<UserInfoDto> getOutgoingFriendRequests(String userId) {
        List<String> outgoingIds = userFriendRepository.findUserIdsOfOutgoingRequests(userId);
        return outgoingIds.stream()
                .map(id -> userService.getUserInfo(id, userId))
                .toList();
    }

    public void removeFriend(String userId, String friendId) {
        if (!userFriendRepository.isFriend(userId, friendId)) return;
        userFriendRepository.deleteFriendship(userId, friendId);
        FriendshipEvent friendshipEvent = new FriendshipEvent(userId, friendId);
        userEventPublisher.publishFriendshipEvent(EventType.FRIENDSHIP_DELETED, friendshipEvent);
    }
}
