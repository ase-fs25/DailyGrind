package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Represents a like relationship for a post in the DynamoDB table.
 * <p>
 * This class is annotated with {@link DynamoDbBean} to indicate it is a DynamoDB entity,
 * and uses {@link DynamoDbPartitionKey} and {@link DynamoDbSortKey} to define the partition and sort keys.
 * It stores a relationship where a user likes a post, identified by the combination of the post ID and user ID.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeEntity {

    // Constants for partition key (PK) and sort key (SK) prefixes and postfixes
    public static String PK_PREFIX = "POST";
    public static String PK_POSTFIX = "LIKE";
    public static String SK_PREFIX = "USER";

    private String pk;
    private String sk;

    /**
     * Returns the partition key (PK) for this like entity.
     * The partition key is constructed from the post ID and predefined prefixes and postfixes.
     *
     * @return the partition key (PK)
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * Returns the sort key (SK) for this like entity.
     * The sort key is constructed from the user ID and a predefined prefix.
     *
     * @return the sort key (SK)
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    /**
     * Generates the partition key (PK) for a like entity.
     * The PK is a combination of the post ID and predefined prefixes and postfixes.
     *
     * @param postId the post ID
     * @return the generated partition key (PK)
     */
    public static String generatePK(String postId) {
        return PK_PREFIX + "#" + postId + "#" + PK_POSTFIX;
    }

    /**
     * Generates the sort key (SK) for a like entity.
     * The SK is a combination of the user ID and a predefined prefix.
     *
     * @param userId the user ID
     * @return the generated sort key (SK)
     */
    public static String generateSK(String userId) {
        return SK_PREFIX + "#" + userId;
    }

    /**
     * Extracts the user ID from the sort key (SK).
     *
     * @return the extracted user ID
     */
    public String getUserId() {
        return sk.split("#")[1];
    }

    /**
     * Extracts the post ID from the partition key (PK).
     *
     * @return the extracted post ID
     */
    public String getPostId() {
        return pk.split("#")[1];
    }

}
