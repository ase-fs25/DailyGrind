package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.FriendRequestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserFriendRepository {

    private final DynamoDbTable<FriendRequestEntity> friendRequestTable;

    // --- Create friend request ---
    public void createFriendRequest(String senderId, String receiverId) {
        FriendRequestEntity request = FriendRequestEntity.builder()
                .pk("FRIEND_REQUEST#" + receiverId)
                .sk(UUID.randomUUID().toString())
                .senderId(senderId)
                .receiverId(receiverId)
                .status("PENDING")
                .build();
        friendRequestTable.putItem(request);
    }

    // --- Accept friend request ---
    public void acceptFriendRequest(String requestId, String receiverId) {
        FriendRequestEntity request = friendRequestTable.getItem(
                Key.builder().partitionValue("FRIEND_REQUEST#" + receiverId).sortValue(requestId).build()
        );
        if (request != null && request.getStatus().equals("PENDING")) {
            request.setStatus("ACCEPTED");
            friendRequestTable.putItem(request);
        }
    }

    // --- Decline friend request ---
    public void declineFriendRequest(String requestId, String receiverId) {
        FriendRequestEntity request = friendRequestTable.getItem(
                Key.builder().partitionValue("FRIEND_REQUEST#" + receiverId).sortValue(requestId).build()
        );
        if (request != null && request.getStatus().equals("PENDING")) {
            request.setStatus("DECLINED");
            friendRequestTable.putItem(request);
        }
    }

    // --- Cancel outgoing friend request ---
    public void cancelFriendRequest(String requestId, String senderId) {
        // Here you would need to delete if you want, or mark as CANCELLED
        FriendRequestEntity request = friendRequestTable.getItem(
                Key.builder().partitionValue("FRIEND_REQUEST#" + senderId).sortValue(requestId).build()
        );
        if (request != null) {
            request.setStatus("CANCELLED");
            friendRequestTable.putItem(request);
        }
    }

    // --- List incoming friend requests ---
    public List<String> findIncomingRequests(String receiverId) {
        QueryConditional query = QueryConditional.keyEqualTo(
                Key.builder().partitionValue("FRIEND_REQUEST#" + receiverId).build()
        );

        return friendRequestTable.query(r -> r.queryConditional(query))
                .items()
                .stream()
                .filter(item -> "PENDING".equals(item.getStatus()))
                .map(FriendRequestEntity::getSenderId)
                .toList();
    }

    // --- List outgoing friend requests ---
    public List<String> findOutgoingRequests(String senderId) {
        // We would need a GSI (Global Secondary Index) here if you want efficient querying
        // For now assume simple scan if needed or alternative design
        throw new UnsupportedOperationException("Outgoing requests require GSI or custom design");
    }

    // --- List friends ---
    public List<String> findFriends(String userId) {
        QueryConditional query = QueryConditional.keyEqualTo(
                Key.builder().partitionValue("FRIEND_REQUEST#" + userId).build()
        );

        return friendRequestTable.query(r -> r.queryConditional(query))
                .items()
                .stream()
                .filter(item -> "ACCEPTED".equals(item.getStatus()))
                .map(FriendRequestEntity::getSenderId)
                .toList();
    }

    public void removeFriend(String userId, String friendId) {
        // Custom logic: you could delete accepted request, or mark as REMOVED
        throw new UnsupportedOperationException("Remove friend logic needs defining!");
    }
}
