package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Entity representing a friendship between two users in the database.
 * <p>
 * This class is used to map a friendship between two users to a DynamoDB table. The partition key (PK)
 * and sort key (SK) are composed in a specific format to ensure proper storage and retrieval of friendships.
 * The PK represents the user, and the SK represents the friend.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendEntity {

    /**
     * Prefix for the partition key representing users.
     */
    public static String PREFIX = "USER";

    /**
     * Suffix for the sort key representing friends.
     */
    public static String POSTFIX = "FRIEND";

    private String pk;
    private String sk;

    /**
     * The partition key (PK) for this friendship.
     * <p>
     * The PK is composed of the user ID, a prefix, and a suffix to ensure proper partitioning of the data
     * in DynamoDB.
     *
     * @return the partition key (PK)
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * The sort key (SK) for this friendship.
     * <p>
     * The SK is composed of the friend ID and a suffix to ensure proper sorting of the friendships in DynamoDB.
     *
     * @return the sort key (SK)
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    /**
     * Generates the partition key (PK) for a friendship between a user and their friend.
     * <p>
     * The PK is composed of the user ID, a prefix, and a suffix to ensure proper partitioning of the
     * friendship data in DynamoDB.
     *
     * @param userId the user ID involved in the friendship
     * @return the generated partition key (PK)
     */
    public static String generatePK(String userId) {
        return PREFIX + "#" + userId + "#" + POSTFIX;
    }

    /**
     * Generates the sort key (SK) for a friendship between two users.
     * <p>
     * The SK is composed of the friend ID and a suffix to ensure proper sorting of the friendships
     * in DynamoDB.
     *
     * @param friendId the friend ID involved in the friendship
     * @return the generated sort key (SK)
     */
    public static String generateSK(String friendId) {
        return POSTFIX + "#" + friendId;
    }

    /**
     * Extracts the user ID from the partition key (PK).
     * <p>
     * The user ID is the second part of the PK after the "#".
     *
     * @return the user ID extracted from the partition key
     */
    public String getUserId() {
        return pk.split("#")[1];
    }

    /**
     * Extracts the friend ID from the sort key (SK).
     * <p>
     * The friend ID is the second part of the SK after the "#".
     *
     * @return the friend ID extracted from the sort key
     */
    public String getFriendId() {
        return sk.split("#")[1];
    }
}
