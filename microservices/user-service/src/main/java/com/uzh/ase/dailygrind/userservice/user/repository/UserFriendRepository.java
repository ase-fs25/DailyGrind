package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.FriendshipEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserFriendRepository {

    private final DynamoDbTable<FriendshipEntity> friendRequestTable;

    public boolean isFriend(String userId, String otherUserId) {
        Key key = Key.builder()
                .partitionValue(FriendshipEntity.generatePK(userId))
                .sortValue(FriendshipEntity.generateSK(otherUserId))
                .build();

        FriendshipEntity friendRequest = friendRequestTable.getItem(key);
        return friendRequest != null && friendRequest.isFriendshipAccepted();
    }

    public List<String> findAllFriends(String userId) {
        QueryConditional query = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(FriendshipEntity.generatePK(userId)).build()
        );

        return friendRequestTable.query(r -> r.queryConditional(query))
                .items()
                .stream()
                .filter(FriendshipEntity::isFriendshipAccepted)
                .map(FriendshipEntity::getFriendId)
                .distinct()
                .toList();
    }

    public void createFriendRequest(String senderId, String receiverId) {
        FriendshipEntity incomingRequest = FriendshipEntity.builder()
            .pk(FriendshipEntity.generatePK(receiverId))
            .sk(FriendshipEntity.generateSK(senderId))
            .friendshipAccepted(false)
            .incoming(true)
            .build();
        friendRequestTable.putItem(incomingRequest);

        FriendshipEntity outgoingRequest = FriendshipEntity.builder()
                .pk(FriendshipEntity.generatePK(senderId))
                .sk(FriendshipEntity.generateSK(receiverId))
                .friendshipAccepted(false)
                .incoming(false)
                .build();
        friendRequestTable.putItem(outgoingRequest);
    }

    public void acceptFriendRequest(String requestingUserId, String senderId) {
        FriendshipEntity incomingRequest = friendRequestTable.getItem(
                Key.builder()
                    .partitionValue(FriendshipEntity.generatePK(requestingUserId))
                    .sortValue(FriendshipEntity.generateSK(senderId))
                    .build()
        );
        if (incomingRequest != null && !incomingRequest.isFriendshipAccepted()) {
            incomingRequest.setFriendshipAccepted(true);
            friendRequestTable.putItem(incomingRequest);
        }

        FriendshipEntity outgoingRequest = friendRequestTable.getItem(
                Key.builder()
                    .partitionValue(FriendshipEntity.generatePK(senderId))
                    .sortValue(FriendshipEntity.generateSK(requestingUserId))
                    .build()
        );
        if (outgoingRequest != null && !outgoingRequest.isFriendshipAccepted()) {
            outgoingRequest.setFriendshipAccepted(true);
            friendRequestTable.putItem(outgoingRequest);
        }

    }

    public void deleteFriendship(String userId, String friendId) {
        FriendshipEntity incomingRequest = friendRequestTable.getItem(
            Key.builder()
                .partitionValue(FriendshipEntity.generatePK(userId))
                .sortValue(FriendshipEntity.generateSK(friendId))
                .build()
        );

        if (incomingRequest != null && !incomingRequest.isFriendshipAccepted()) {
            friendRequestTable.deleteItem(incomingRequest);
        }

        FriendshipEntity outgoingRequest = friendRequestTable.getItem(
            Key.builder()
                .partitionValue(FriendshipEntity.generatePK(friendId))
                .sortValue(FriendshipEntity.generateSK(userId))
                .build()
        );

        if (outgoingRequest != null && !outgoingRequest.isFriendshipAccepted()) {
            friendRequestTable.deleteItem(outgoingRequest);
        }

    }

    public List<String> findUserIdsOfIncomingRequests(String userId) {
        QueryConditional query = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(FriendshipEntity.generatePK(userId)).build()
        );

        return friendRequestTable.query(r -> r.queryConditional(query))
                .items()
                .stream()
                .filter(FriendshipEntity::isIncoming)
                .filter(item -> !item.isFriendshipAccepted())
                .map(FriendshipEntity::getSenderId)
                .distinct()
                .toList();
    }

    public List<String> findUserIdsOfOutgoingRequests(String userId) {
        QueryConditional query = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(FriendshipEntity.generatePK(userId)).build()
        );

        return friendRequestTable.query(r -> r.queryConditional(query))
                .items()
                .stream()
                .filter(item -> !item.isIncoming())
                .filter(item -> !item.isFriendshipAccepted())
                .map(FriendshipEntity::getReceiverId)
                .distinct()
                .toList();
    }

    public List<String> findFriends(String userId) {
        QueryConditional query = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(FriendshipEntity.generatePK(userId)).build()
        );

        return friendRequestTable.query(r -> r.queryConditional(query))
                .items()
                .stream()
                .filter(FriendshipEntity::isFriendshipAccepted)
                .map(FriendshipEntity::getFriendId)
                .distinct()
                .toList();
    }

    public boolean existsPendingRequest(String senderId, String receiverId) {
        FriendshipEntity request = friendRequestTable.getItem(
                Key.builder()
                        .partitionValue(FriendshipEntity.generatePK(senderId))
                        .sortValue(FriendshipEntity.generateSK(receiverId))
                        .build()
        );

        return request != null && !request.isFriendshipAccepted();
    }

}
