package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollowingEntity {

    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "FOLLOWING";
    public static final String SK_PREFIX = "USER";

    private String pk;
    private String sk;

    public static String generatePK(String userId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX;
    }

    public static String generateSK(String followerId) {
        return SK_PREFIX + "#" + followerId;
    }

    public String getId() {
        return sk.split("#")[1];
    }

}
