package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

/**
 * Represents a user's basic information in the DynamoDB database.
 * <p>
 * This entity stores essential user information such as email, name, birthday, profile picture URL,
 * location, and number of friends. It also includes the partition key (PK) and sort key (SK)
 * for DynamoDB and utility methods for key generation.
 * </p>
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    // Constants for generating PK and SK
    public static final String PK_PREFIX = "USER";
    public static final String SK_PREFIX = "INFO";

    private String pk;
    private String sk;

    /**
     * Retrieves the partition key (PK) for the user entity.
     *
     * @return the partition key (PK) for the user entity.
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * Retrieves the sort key (SK) for the user entity.
     *
     * @return the sort key (SK) for the user entity.
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
    private String birthday;
    private String location;
    private int numFriends;

    /**
     * Generates the partition key (PK) for a given user ID.
     * The PK format is "USER#userId".
     *
     * @param id the user ID to generate the PK.
     * @return the generated PK.
     */
    public static String generatePK(String id) {
        return PK_PREFIX + "#" + id;
    }

    /**
     * Generates the sort key (SK) for the user entity.
     * The SK format is "INFO".
     *
     * @return the generated SK.
     */
    public static String generateSK() {
        return SK_PREFIX;
    }

    /**
     * Retrieves the user ID by parsing the partition key (PK).
     *
     * @return the user ID.
     */
    public String getId() {
        return pk.split("#")[1];
    }
}
