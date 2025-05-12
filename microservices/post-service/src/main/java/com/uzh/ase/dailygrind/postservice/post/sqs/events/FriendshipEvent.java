package com.uzh.ase.dailygrind.postservice.post.sqs.events;

/**
 * Represents an event that indicates a change in the friendship status between two users.
 * This event contains the user IDs of two users, `userAId` and `userBId`, who have established or
 * removed a friendship.
 */
public record FriendshipEvent(
    /**
     * The ID of the first user involved in the friendship event (User A).
     */
    String userAId,

    /**
     * The ID of the second user involved in the friendship event (User B).
     */
    String userBId
) {
}
