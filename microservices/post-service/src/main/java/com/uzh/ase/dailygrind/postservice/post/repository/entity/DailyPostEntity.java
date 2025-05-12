package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import com.uzh.ase.dailygrind.postservice.post.util.TimeToLiveHelper;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

/**
 * Entity representing a daily post in the database.
 * <p>
 * This class is used to map a daily post to a DynamoDB table. The partition key (PK) and sort key (SK)
 * are composed in a specific format to ensure the correct storage and retrieval of daily posts related
 * to users. It also includes a time-to-live (TTL) attribute that determines when the item will expire.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPostEntity {

    /**
     * Prefix for the partition key representing users.
     */
    public static final String PK_PREFIX = "USER";

    /**
     * Suffix for the partition key representing daily posts.
     */
    public static final String PK_SUFFIX = "DAILY";

    /**
     * Prefix for the sort key representing posts.
     */
    public static final String SK_PREFIX = "POST";

    private String pk;
    private String sk;

    /**
     * The time-to-live (TTL) value for this daily post.
     * <p>
     * TTL determines when the item will expire in DynamoDB.
     * The value is calculated using the {@link TimeToLiveHelper#getTimeToLive()} method.
     */
    private long ttl;

    /**
     * Constructs a new DailyPostEntity for the given user and post IDs.
     * <p>
     * This constructor generates the partition key (PK) and sort key (SK) based on the user and post IDs,
     * and sets the TTL value using the {@link TimeToLiveHelper#getTimeToLive()} method.
     *
     * @param userId the user ID associated with the post
     * @param postId the post ID associated with the daily post
     */
    public DailyPostEntity(String userId, String postId) {
        this.pk = generatePK(userId);
        this.sk = generateSK(postId);
        this.ttl = TimeToLiveHelper.getTimeToLive();
    }

    /**
     * Generates the partition key (PK) for a daily post.
     * <p>
     * The PK is composed of the user ID, a prefix, and a suffix to ensure proper partitioning of the
     * data in DynamoDB.
     *
     * @param userId the user ID associated with the daily post
     * @return the generated partition key (PK)
     */
    public static String generatePK(String userId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX;
    }

    /**
     * Generates the sort key (SK) for a daily post.
     * <p>
     * The SK is composed of the post ID and a prefix to ensure proper sorting of the posts in DynamoDB.
     *
     * @param postId the post ID associated with the daily post
     * @return the generated sort key (SK)
     */
    public static String generateSK(String postId) {
        return SK_PREFIX + "#" + postId;
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
     * Extracts the post ID from the sort key (SK).
     * <p>
     * The post ID is the second part of the SK after the "#".
     *
     * @return the post ID extracted from the sort key
     */
    public String getPostId() {
        return sk.split("#")[1];
    }

}
