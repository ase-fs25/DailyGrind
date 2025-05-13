package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import com.uzh.ase.dailygrind.postservice.post.util.TimeToLiveHelper;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

/**
 * Represents a daily post entity in the DynamoDB table.
 * <p>
 * This class is annotated with {@link DynamoDbBean} to indicate it is a DynamoDB entity,
 * and uses {@link DynamoDbPartitionKey} and {@link DynamoDbSortKey} to define the partition and sort keys.
 * Additionally, it includes a time-to-live (TTL) field for expiration management.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPostEntity {

    // Constants for partition key (PK) and sort key (SK) prefixes and suffixes
    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "DAILY";
    public static final String SK_PREFIX = "POST";

    private String pk;
    private String sk;

    private long ttl;

    /**
     * Returns the partition key (PK) for this daily post entity.
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
     * Returns the sort key (SK) for this daily post entity.
     * The sort key is constructed from the post ID.
     *
     * @return the sort key (SK)
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    /**
     * Constructs a DailyPostEntity object with the provided user ID and post ID.
     * The partition key (PK) and sort key (SK) are generated using the user ID and post ID respectively.
     * The TTL (Time-to-Live) value is also set using the {@link TimeToLiveHelper}.
     *
     * @param userId the user ID
     * @param postId the post ID
     */
    public DailyPostEntity(String userId, String postId) {
        this.pk = generatePK(userId);
        this.sk = generateSK(postId);
        this.ttl = TimeToLiveHelper.getTimeToLive();
    }

    /**
     * Generates the partition key (PK) for a daily post entity.
     * The PK is a combination of the user ID and predefined prefixes and suffixes.
     *
     * @param userId the user ID
     * @return the generated partition key (PK)
     */
    public static String generatePK(String userId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX;
    }

    /**
     * Generates the sort key (SK) for a daily post entity.
     * The SK is a combination of the post ID and a predefined prefix.
     *
     * @param postId the post ID
     * @return the generated sort key (SK)
     */
    public static String generateSK(String postId) {
        return SK_PREFIX + "#" + postId;
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
