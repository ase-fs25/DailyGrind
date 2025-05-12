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
public class LikeEntity {

    public static String PK_PREFIX = "POST";

    public static String PK_POSTFIX = "LIKE";

    public static String SK_PREFIX = "USER";

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

    public static String generatePK(String postId) {
        return PK_PREFIX + "#" + postId + "#" + PK_POSTFIX;
    }

    public static String generateSK(String userId) {
        return SK_PREFIX + "#" + userId;
    }

    public String getUserId() {
        return sk.split("#")[1];
    }

    public String getPostId() {
        return pk.split("#")[1];
    }

}
