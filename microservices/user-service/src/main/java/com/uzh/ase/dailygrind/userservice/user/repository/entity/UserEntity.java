package com.uzh.ase.dailygrind.userservice.user.repository.entity;

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
public class UserEntity {

    public static final String TABLE_NAME = "users";
    public static final String PK_PREFIX = "USER";
    public static final String SK_PREFIX = "INFO";

    private String pk;
    private String sk;

    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String birthday;
    private String location;
    private int numFollowers;
    private int numFollowing;

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
