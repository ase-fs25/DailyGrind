package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

/**
 * Represents a comment entity in the DynamoDB table.
 * <p>
 * This class is annotated with {@link DynamoDbBean} to indicate it is a DynamoDB entity, and uses
 * {@link DynamoDbPartitionKey} and {@link DynamoDbSortKey} to define the partition and sort keys.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity {

    // SK has the following format: COMMENT#<commentId>
    public static final String SK_PREFIX = "COMMENT";
    // PK has the following format: USER#<userId>#POST#<postId>#COMMENT
    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "POST";

    private String pk;
    private String sk;

    /**
     * Returns the partition key (PK) for this comment entity.
     * The partition key is constructed from the user ID and post ID.
     *
     * @return the partition key (PK)
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * Returns the sort key (SK) for this comment entity.
     * The sort key is constructed from a unique comment ID.
     *
     * @return the sort key (SK)
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    private String commentContent;
    private String commentTimestamp;

    /**
     * Generates the partition key (PK) for a comment entity.
     * The PK is a combination of the user ID, post ID, and predefined prefixes and suffixes.
     *
     * @param userId the user ID
     * @param postId the post ID
     * @return the generated partition key (PK)
     */
    public static String generatePK(String userId, String postId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX + "#" + postId + "#" + SK_PREFIX;
    }

    /**
     * Generates the sort key (SK) for a comment entity.
     * The SK is a combination of a predefined prefix and a unique comment ID.
     *
     * @param commentId the comment ID
     * @return the generated sort key (SK)
     */
    public static String generateSK(String commentId) {
        if (commentId == null || commentId.isEmpty()) commentId = UUID.randomUUID().toString();
        return SK_PREFIX + "#" + commentId;
    }

    /**
     * Extracts the comment ID from the sort key (SK).
     *
     * @return the extracted comment ID
     */
    public String getCommentId() {
        return sk.split("#")[1];
    }

    /**
     * Extracts the post ID from the partition key (PK).
     *
     * @return the extracted post ID
     */
    public String getPostId() {
        return pk.split("#")[3];
    }

    /**
     * Extracts the user ID from the partition key (PK).
     *
     * @return the extracted user ID
     */
    public String getUserId() {
        return pk.split("#")[1];
    }

}
