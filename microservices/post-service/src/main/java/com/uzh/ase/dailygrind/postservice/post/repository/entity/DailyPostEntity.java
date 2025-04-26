package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPostEntity {
    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "DAILYPOST";
    public static final String SK_PREFIX = "POST";

    private String pk;
    private String sk;

    private Long ttl;

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
