package com.uzh.ase.dailygrind.userservice.user.sns.events;

/**
 * Represents an event related to a friendship action, such as creation or deletion.
 * <p>
 * This event is published to notify other services of changes in the friendship state
 * between two users.
 *
 * @param userAId The ID of the first user involved in the friendship.
 * @param userBId The ID of the second user involved in the friendship.
 */
public record FriendshipEvent(
    String userAId,
    String userBId
) {
}
