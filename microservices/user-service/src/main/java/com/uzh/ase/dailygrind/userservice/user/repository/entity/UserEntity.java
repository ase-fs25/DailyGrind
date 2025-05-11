package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    public static final String PK_PREFIX = "USER";
    public static final String SK_PREFIX = "INFO";

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;

    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;

    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String birthday;
    private String location;
    private int numFriends;

    public static String generatePK(String id) {
        return PK_PREFIX + "#" + id;
    }

    public static String generateSK() {
        return SK_PREFIX;
    }

    public String getId() {
        return pk.split("#")[1];
    }
}
