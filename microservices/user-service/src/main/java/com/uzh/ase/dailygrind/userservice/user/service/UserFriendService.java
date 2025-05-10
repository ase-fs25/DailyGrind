package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFriendRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.FriendRequestEntity;

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
    // --- Friend Request actions ---

public void sendFriendRequest(String senderId, String receiverId) {
    if (userFriendRepository.existsPendingRequest(senderId, receiverId)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request already sent.");
    }
    userFriendRepository.createFriendRequest(senderId, receiverId);
}


  public void acceptFriendRequest(String requestId, String receiverId) {
    // Step 1: mark the original request as accepted
    userFriendRepository.acceptFriendRequest(requestId, receiverId);

    // Step 2: fetch senderId from the accepted request
    // We do this by reloading the request entity
    FriendRequestEntity request = userFriendRepository.getRequestById(requestId, receiverId);
    if (request == null || !"ACCEPTED".equals(request.getStatus())) {
        throw new RuntimeException("Friend request not found or not accepted.");
    }

    String senderId = request.getSenderId();

    // Step 3: add reciprocal friendship entries
    userFriendRepository.addFriendship(receiverId, senderId);
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
        List<FriendRequestEntity> requests = userFriendRepository.findIncomingRequests(userId);

        return requests.stream()
            .map(req -> {
                UserInfoDto sender = userService.getUserInfo(req.getSenderId(), userId);
                return new UserInfoDto(
                    sender.userId(),
                    sender.email(),
                    sender.firstName(),
                    sender.lastName(),
                    sender.birthday(),
                    sender.location(),
                    sender.numberOfFriends(),
                    sender.profilePictureUrl(),
                    sender.isFriend(),
                    req.getSk()
                );
            })
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
