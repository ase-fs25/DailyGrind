package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Represents a pinned post relationship in the DynamoDB table.
 * <p>
 * This class is annotated with {@link DynamoDbBean} to indicate it is a DynamoDB entity,
 * and uses {@link DynamoDbPartitionKey} and {@link DynamoDbSortKey} to define the partition and sort keys.
 * It stores a relationship where a user pins a post, identified by the combination of the user ID and post ID.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@Builder
public class PinnedPostEntity {

    // Constants for partition key (PK) and sort key (SK) prefixes and suffixes
    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "PINNED";
    public static final String SK_PREFIX = "POST";

    private String pk;
    private String sk;

    /**
     * Returns the partition key (PK) for this pinned post entity.
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
     * Returns the sort key (SK) for this pinned post entity.
     * The sort key is constructed from the post ID and a predefined prefix.
     *
     * @return the sort key (SK)
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    /**
     * Constructor to create a new {@link PinnedPostEntity} using user ID and post ID.
     *
     * @param userId the user ID
     * @param postId the post ID
     */
    public PinnedPostEntity(String userId, String postId) {
        this.pk = generatePK(userId);
        this.sk = generateSK(postId);
    }

    /**
     * Generates the partition key (PK) for a pinned post entity.
     * The PK is a combination of the user ID and predefined prefixes and suffixes.
     *
     * @param userId the user ID
     * @return the generated partition key (PK)
     */
    public static String generatePK(String userId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX;
    }

    /**
     * Generates the sort key (SK) for a pinned post entity.
     * The SK is a combination of the post ID and a predefined prefix.
     *
     * @param postId the post ID
     * @return the generated sort key (SK)
     */
    public static String generateSK(String postId) {
        return SK_PREFIX + "#" + postId;
    }

    /**
     * Extracts the post ID from the sort key (SK).
     *
     * @return the extracted post ID
     */
    public String getId() {
        return sk.split("#")[1];
    }

}
