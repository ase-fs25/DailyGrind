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
public class CommentEntity {

    public static final String SK_PREFIX = "COMMENT";

    public static final String PK_PREFIX = "USER";

    public static final String PK_SUFFIX = "POST";

    @Getter(onMethod_ =  {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;
    @Getter(onMethod_ =  {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;

    private String commentContent;
    private String commentTimestamp;

    public static String generatePK(String userId, String postId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX + "#" + postId + "#" + SK_PREFIX;
    }

    public static String generateSK(String commentId) {
        if (commentId == null || commentId.isEmpty()) commentId = UUID.randomUUID().toString();
        return SK_PREFIX + "#" + commentId;
    }

    public String getCommentId() {
        return sk.split("#")[1];
    }

    public String getPostId() {
        return pk.split("#")[3];
    }

    public String getUserId() {
        return pk.split("#")[1];
    }

}
