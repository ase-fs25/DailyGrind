package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@Builder
public class PinnedPostEntity {

    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "PINNED";
    public static final String SK_PREFIX = "POST";

    @Getter(onMethod_ =  {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;
    @Getter(onMethod_ =  {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;

    public PinnedPostEntity(String userId, String postId) {
        this.pk = generatePK(userId);
        this.sk = generateSK(postId);
    }

    public static String generatePK(String userId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX;
    }

    public static String generateSK(String postId) {
        return SK_PREFIX + "#" + postId;
    }

    public String getId() {
        return sk.split("#")[1];
    }

}
