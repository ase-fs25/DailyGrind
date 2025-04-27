package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity {

    public static String TABLE_NAME = "posts";

    public static String PREFIX = "USER";

    public static String POSTFIX = "POST";

    @Getter(onMethod_ =  {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;
    @Getter(onMethod_ =  {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;

    private String postTitle;
    private String postContent;
    private String postTimestamp;

    private Long likeCount;
    private Long commentCount;

    public static String generatePK(String userId) {
        return PREFIX + "#" + userId + "#" + POSTFIX;
    }

    public static String generateSK(String postId) {
        if (postId == null || postId.isEmpty()) postId = UUID.randomUUID().toString();
        return POSTFIX + "#" + postId;
    }

    public String getUserId() {
        return pk.split("#")[1];
    }

    public String getPostId() {
        return sk.split("#")[1];
    }

}
