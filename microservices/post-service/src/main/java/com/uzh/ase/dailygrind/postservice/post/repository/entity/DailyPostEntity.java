package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import com.uzh.ase.dailygrind.postservice.post.util.TimeToLiveHelper;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPostEntity {

    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "DAILY";
    public static final String SK_PREFIX = "POST";

    @Getter(onMethod_ =  {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;

    @Getter(onMethod_ =  {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;

    private long ttl;

    public DailyPostEntity(String userId, String postId) {
        this.pk = generatePK(userId);
        this.sk = generateSK(postId);
        this.ttl = TimeToLiveHelper.getTimeToLive();
    }

    public static String generatePK(String userId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX;
    }

    public static String generateSK(String postId) {
        return SK_PREFIX + "#" + postId;
    }

    public String getUserId() {
        return pk.split("#")[1];
    }

    public String getPostId() {
        return sk.split("#")[1];
    }

}
