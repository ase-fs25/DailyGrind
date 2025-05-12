package com.uzh.ase.dailygrind.postservice.post.repository.entity;

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
public class FriendEntity {

    public static String PREFIX = "USER";

    public static String POSTFIX = "FRIEND";

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

    public static String generatePK(String userId) {
        return PREFIX + "#" + userId + "#" + POSTFIX;
    }

    public static String generateSK(String friendId) {
        return POSTFIX + "#" + friendId;
    }

    public String getUserId() {
        return pk.split("#")[1];
    }

    public String getFriendId() {
        return sk.split("#")[1];
    }

}
