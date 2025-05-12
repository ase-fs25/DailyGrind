package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Represents a friendship entity in the DynamoDB database.
 * <p>
 * This entity stores the information about a friendship request between two users,
 * including the primary key (PK) and sort key (SK), friendship status, and the direction of the request.
 * </p>
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipEntity {

    // Constants for generating PK and SK
    public static final String PK_PREFIX = "USER";
    public static final String PK_POSTFIX = "FRIEND";

    private String pk;
    private String sk;

    /**
     * Retrieves the partition key (PK) for the friendship entity.
     *
     * @return the partition key (PK) for the friendship entity.
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * Retrieves the sort key (SK) for the friendship entity.
     *
     * @return the sort key (SK) for the friendship entity.
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    private boolean incoming; // incoming or outgoing request
    private boolean friendshipAccepted; // true for friends and false for pending requests

    /**
     * Gets the sender's user ID based on the direction of the friendship request.
     *
     * @return the sender's user ID.
     */
    public String getSenderId(){
        return incoming ? sk.split("#")[1] : pk.split("#")[1];
    }

    /**
     * Gets the receiver's user ID based on the direction of the friendship request.
     *
     * @return the receiver's user ID.
     */
    public String getReceiverId(){
        return incoming ? pk.split("#")[1] : sk.split("#")[1];
    }

    /**
     * Gets the friend ID (either sender or receiver, depending on the direction).
     *
     * @return the friend ID.
     */
    public String getFriendId(){
        return sk.split("#")[1];
    }

    /**
     * Generates the partition key (PK) for a given user ID.
     * The PK format is "USER#userId#FRIEND".
     *
     * @param userid the user ID to generate the PK.
     * @return the generated PK.
     */
    public static String generatePK(String userid) {
        return PK_PREFIX + "#" + userid + "#" + PK_POSTFIX;
    }

    /**
     * Generates the sort key (SK) for a given user ID.
     * The SK format is "USER#userId".
     *
     * @param userId the user ID to generate the SK.
     * @return the generated SK.
     */
    public static String generateSK(String userId) {
        return PK_PREFIX + "#" + userId;
    }
}
