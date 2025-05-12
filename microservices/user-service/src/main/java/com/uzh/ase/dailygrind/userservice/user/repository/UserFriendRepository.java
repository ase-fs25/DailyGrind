package com.uzh.ase.dailygrind.userservice.user.repository;

import com.uzh.ase.dailygrind.userservice.user.repository.entity.FriendshipEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

/**
 * Repository for managing user friendship entities in DynamoDB.
 * Provides operations related to friend requests, friendships, and user relationships.
 */
@Repository
@RequiredArgsConstructor
public class UserFriendRepository {

    private final DynamoDbTable<FriendshipEntity> friendRequestTable;

    /**
     * Checks if two users are friends.
     *
     * @param userId     the ID of the first user
     * @param otherUserId the ID of the second user
     * @return {@code true} if the users are friends and the friendship is accepted, {@code false} otherwise
     */
    public boolean isFriend(String userId, String otherUserId) {
        if (userId.equals(otherUserId)) return false;
        Key key = Key.builder()
            .partitionValue(FriendshipEntity.generatePK(userId))
            .sortValue(FriendshipEntity.generateSK(otherUserId))
            .build();

        FriendshipEntity friendRequest = friendRequestTable.getItem(key);
        return friendRequest != null && friendRequest.isFriendshipAccepted();
    }

    /**
     * Retrieves all friends of a specified user.
     *
     * @param userId the ID of the user whose friends are to be retrieved
     * @return a list of user IDs representing the user's friends
     */
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

    /**
     * Creates a new friend request between two users.
     * Adds both incoming and outgoing friendship requests.
     *
     * @param senderId   the ID of the user sending the friend request
     * @param receiverId the ID of the user receiving the friend request
     */
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

    /**
     * Accepts a pending friend request between two users.
     *
     * @param requestingUserId the ID of the user requesting the friendship
     * @param senderId         the ID of the user sending the friend request
     */
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

    /**
     * Deletes the friendship (both incoming and outgoing requests) between two users.
     *
     * @param userId   the ID of one user
     * @param friendId the ID of the other user
     */
    public void deleteFriendship(String userId, String friendId) {
        FriendshipEntity incomingRequest = friendRequestTable.getItem(
            Key.builder()
                .partitionValue(FriendshipEntity.generatePK(userId))
                .sortValue(FriendshipEntity.generateSK(friendId))
                .build()
        );

        if (incomingRequest != null) {
            friendRequestTable.deleteItem(incomingRequest);
        }

        FriendshipEntity outgoingRequest = friendRequestTable.getItem(
            Key.builder()
                .partitionValue(FriendshipEntity.generatePK(friendId))
                .sortValue(FriendshipEntity.generateSK(userId))
                .build()
        );

        if (outgoingRequest != null) {
            friendRequestTable.deleteItem(outgoingRequest);
        }
    }

    /**
     * Retrieves a list of user IDs who have sent an incoming friend request to the specified user.
     *
     * @param userId the ID of the user receiving the friend requests
     * @return a list of user IDs representing the senders of incoming friend requests
     */
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

    /**
     * Retrieves a list of user IDs who have sent an outgoing friend request to the specified user.
     *
     * @param userId the ID of the user who has sent the outgoing friend requests
     * @return a list of user IDs representing the recipients of outgoing friend requests
     */
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

    /**
     * Retrieves a list of user IDs who are friends with the specified user.
     *
     * @param userId the ID of the user whose friends are to be retrieved
     * @return a list of user IDs representing the friends of the user
     */
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

    /**
     * Checks if there is a pending friend request between two users.
     *
     * @param senderId   the ID of the user sending the friend request
     * @param receiverId the ID of the user receiving the friend request
     * @return {@code true} if a pending friend request exists, {@code false} otherwise
     */
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
