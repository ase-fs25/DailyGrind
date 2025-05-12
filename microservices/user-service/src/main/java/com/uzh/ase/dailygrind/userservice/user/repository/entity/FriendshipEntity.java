package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipEntity {

    public static final String PK_PREFIX = "USER";
    public static final String PK_POSTFIX = "FRIEND";

    private String pk;
    private String sk;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    private boolean incoming; // incoming or outgoing request
    private boolean friendshipAccepted; // true for friends and false for pending requests

    public String getSenderId(){
        return incoming ? sk.split("#")[1] : pk.split("#")[1];
    }

    public String getReceiverId(){
        return incoming ? pk.split("#")[1] : sk.split("#")[1];
    }

    public String getFriendId(){
        return sk.split("#")[1];
    }

    public static String generatePK(String userid) {
        return PK_PREFIX + "#" + userid + "#" + PK_POSTFIX;
    }

    public static String generateSK(String userId) {
        return PK_PREFIX + "#" + userId;
    }

}
