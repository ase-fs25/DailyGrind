package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Represents a friend relationship entity in the DynamoDB table.
 * <p>
 * This class is annotated with {@link DynamoDbBean} to indicate it is a DynamoDB entity,
 * and uses {@link DynamoDbPartitionKey} and {@link DynamoDbSortKey} to define the partition and sort keys.
 * It represents the friendship between two users and is used to store their relationship in DynamoDB.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendEntity {

    // Constants for partition key (PK) and sort key (SK) prefixes and postfixes
    public static String PREFIX = "USER";
    public static String POSTFIX = "FRIEND";

    private String pk;
    private String sk;

    /**
     * Returns the partition key (PK) for this friend entity.
     * The partition key is constructed from the user ID and predefined prefixes and postfixes.
     *
     * @return the partition key (PK)
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * Returns the sort key (SK) for this friend entity.
     * The sort key is constructed from the friend ID and a predefined postfix.
     *
     * @return the sort key (SK)
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    /**
     * Generates the partition key (PK) for a friend entity.
     * The PK is a combination of the user ID and predefined prefixes and postfixes.
     *
     * @param userId the user ID
     * @return the generated partition key (PK)
     */
    public static String generatePK(String userId) {
        return PREFIX + "#" + userId + "#" + POSTFIX;
    }

    /**
     * Generates the sort key (SK) for a friend entity.
     * The SK is a combination of the friend ID and a predefined postfix.
     *
     * @param friendId the friend ID
     * @return the generated sort key (SK)
     */
    public static String generateSK(String friendId) {
        return POSTFIX + "#" + friendId;
    }

    /**
     * Extracts the user ID from the partition key (PK).
     *
     * @return the extracted user ID
     */
    public String getUserId() {
        return pk.split("#")[1];
    }

    /**
     * Extracts the friend ID from the sort key (SK).
     *
     * @return the extracted friend ID
     */
    public String getFriendId() {
        return sk.split("#")[1];
    }

}
