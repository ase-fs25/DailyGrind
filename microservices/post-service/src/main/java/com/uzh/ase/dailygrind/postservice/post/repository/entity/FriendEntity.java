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

    @Getter(onMethod_ =  {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;

    @Getter(onMethod_ =  {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;

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
