package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

/**
 * Represents a post entity in the DynamoDB table.
 * <p>
 * This class is annotated with {@link DynamoDbBean} to indicate it is a DynamoDB entity,
 * and uses {@link DynamoDbPartitionKey} and {@link DynamoDbSortKey} to define the partition and sort keys.
 * It stores information about a post made by a user, identified by the combination of the user ID and post ID.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity {

    // Constants for partition key (PK) and sort key (SK) prefixes and suffixes
    public static String PREFIX = "USER";
    public static String POSTFIX = "POST";

    private String pk;
    private String sk;

    /**
     * Returns the partition key (PK) for this post entity.
     * The partition key is constructed from the user ID and predefined prefixes and suffixes.
     *
     * @return the partition key (PK)
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * Returns the sort key (SK) for this post entity.
     * The sort key is constructed from the post ID and a predefined prefix.
     *
     * @return the sort key (SK)
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    private String postTitle;
    private String postContent;
    private String postTimestamp;

    private Long likeCount;
    private Long commentCount;

    /**
     * Generates the partition key (PK) for a post entity.
     * The PK is a combination of the user ID and predefined prefixes and suffixes.
     *
     * @param userId the user ID
     * @return the generated partition key (PK)
     */
    public static String generatePK(String userId) {
        return PREFIX + "#" + userId + "#" + POSTFIX;
    }

    /**
     * Generates the sort key (SK) for a post entity.
     * The SK is a combination of the post ID and a predefined prefix.
     *
     * @param postId the post ID
     * @return the generated sort key (SK)
     */
    public static String generateSK(String postId) {
        if (postId == null || postId.isEmpty()) postId = UUID.randomUUID().toString();
        return POSTFIX + "#" + postId;
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
     * Extracts the post ID from the sort key (SK).
     *
     * @return the extracted post ID
     */
    public String getPostId() {
        return sk.split("#")[1];
    }

}
