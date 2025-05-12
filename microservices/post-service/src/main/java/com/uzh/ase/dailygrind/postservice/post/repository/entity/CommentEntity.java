package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

/**
 * Entity representing a comment in the database.
 * <p>
 * This class is used to map a comment to a DynamoDB table. The partition key (PK) and sort key (SK)
 * are composed in a specific format to ensure the correct storage and retrieval of comments in relation
 * to users and posts. It contains the content of the comment, the timestamp, and methods to generate keys
 * for the DynamoDB entity.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity {

    /**
     * Prefix for the sort key representing comments.
     */
    public static final String SK_PREFIX = "COMMENT";

    /**
     * Prefix for the partition key representing users.
     */
    public static final String PK_PREFIX = "USER";

    /**
     * Suffix for the partition key representing posts.
     */
    public static final String PK_SUFFIX = "POST";

    private String pk;
    private String sk;

    /**
     * Returns the partition key (PK) for this comment entity.
     * <p>
     * The PK is generated in the format: "USER#userId#POST#postId#COMMENT"
     *
     * @return the partition key (PK) of the comment entity
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * Returns the sort key (SK) for this comment entity.
     * <p>
     * The SK is generated in the format: "COMMENT#commentId"
     *
     * @return the sort key (SK) of the comment entity
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    private String commentContent;
    private String commentTimestamp;

    /**
     * Generates the partition key (PK) for a comment.
     * <p>
     * The PK is composed of the user ID, post ID, and a prefix and suffix to ensure proper partitioning
     * of the data in DynamoDB.
     *
     * @param userId the user ID associated with the comment
     * @param postId the post ID associated with the comment
     * @return the generated partition key (PK)
     */
    public static String generatePK(String userId, String postId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX + "#" + postId + "#" + SK_PREFIX;
    }

    /**
     * Generates the sort key (SK) for a comment.
     * <p>
     * The SK is composed of a comment ID, which can be either provided or generated if not present.
     *
     * @param commentId the comment ID (if provided)
     * @return the generated sort key (SK)
     */
    public static String generateSK(String commentId) {
        if (commentId == null || commentId.isEmpty()) commentId = UUID.randomUUID().toString();
        return SK_PREFIX + "#" + commentId;
    }

    /**
     * Extracts the comment ID from the sort key (SK).
     * <p>
     * The comment ID is the second part of the SK after the "#".
     *
     * @return the comment ID extracted from the sort key
     */
    public String getCommentId() {
        return sk.split("#")[1];
    }

    /**
     * Extracts the post ID from the partition key (PK).
     * <p>
     * The post ID is the fourth part of the PK after the "#".
     *
     * @return the post ID extracted from the partition key
     */
    public String getPostId() {
        return pk.split("#")[3];
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

}
