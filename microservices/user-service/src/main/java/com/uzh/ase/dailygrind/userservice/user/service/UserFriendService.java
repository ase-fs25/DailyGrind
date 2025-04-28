package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFriendService {

    private final UserFriendRepository userFriendRepository;
    private final UserService userService; // Assuming you have this for fetching UserInfo

    // --- Friend Request actions ---

    public void sendFriendRequest(String senderId, String receiverId) {
        userFriendRepository.createFriendRequest(senderId, receiverId);
    }

    public void acceptFriendRequest(String requestId, String receiverId) {
        userFriendRepository.acceptFriendRequest(requestId, receiverId);
    }

    public void declineFriendRequest(String requestId, String receiverId) {
        userFriendRepository.declineFriendRequest(requestId, receiverId);
    }

    public void cancelFriendRequest(String requestId, String senderId) {
        userFriendRepository.cancelFriendRequest(requestId, senderId);
    }

    // --- Listing friends and requests ---

    public List<UserInfoDto> getFriends(String userId) {
        List<String> friendIds = userFriendRepository.findFriends(userId);
        return friendIds.stream()
                .map(id -> userService.getUserInfo(id, userId))
                .toList();
    }

    public List<UserInfoDto> getIncomingFriendRequests(String userId) {
        List<String> incomingIds = userFriendRepository.findIncomingRequests(userId);
        return incomingIds.stream()
                .map(id -> userService.getUserInfo(id, userId))
                .toList();
    }

    public List<UserInfoDto> getOutgoingFriendRequests(String userId) {
        List<String> outgoingIds = userFriendRepository.findOutgoingRequests(userId);
        return outgoingIds.stream()
                .map(id -> userService.getUserInfo(id, userId))
                .toList();
    }

    public void removeFriend(String userId, String friendId) {
        userFriendRepository.removeFriend(userId, friendId);
    }
}
