package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Represents a user entity in the DynamoDB table.
 * <p>
 * This class is annotated with {@link DynamoDbBean} to indicate it is a DynamoDB entity,
 * and uses {@link DynamoDbPartitionKey} and {@link DynamoDbSortKey} to define the partition and sort keys.
 * It stores information about a user, such as their email, first name, last name, and profile picture URL.
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    // Constants for partition key (PK) and sort key (SK) prefixes and suffixes
    public static String PREFIX = "USER";
    public static String POSTFIX = "INFO";

    private String pk;
    private String sk;

    /**
     * Returns the partition key (PK) for this user entity.
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
     * Returns the sort key (SK) for this user entity.
     * The sort key is a constant value representing user information.
     *
     * @return the sort key (SK)
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;

    /**
     * Generates the partition key (PK) for a user entity.
     * The PK is a combination of the user ID and predefined prefixes and suffixes.
     *
     * @param userId the user ID
     * @return the generated partition key (PK)
     */
    public static String generatePK(String userId) {
        return PREFIX + "#" + userId + "#" + POSTFIX;
    }

    /**
     * Returns the sort key (SK) for a user entity, which is constant for all user records.
     *
     * @return the generated sort key (SK)
     */
    public static String generateSK() {
        return POSTFIX;
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
