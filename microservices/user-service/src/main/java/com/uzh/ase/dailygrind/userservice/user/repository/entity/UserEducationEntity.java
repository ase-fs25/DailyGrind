package com.uzh.ase.dailygrind.userservice.user.repository.entity;

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
public class UserEducationEntity {

    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "EDUCATION";

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

    private String institution;
    private String educationLocation;
    private String degree;
    private String fieldOfStudy;
    private String educationStartDate;
    private String educationEndDate;
    private String educationDescription;

    public static String generatePK(String id) {
        return PK_PREFIX + "#" + id + "#" + PK_SUFFIX;
    }

    public static String generateSK(String educationId) {
        if (educationId == null || educationId.isEmpty()) educationId = UUID.randomUUID().toString();
        return PK_SUFFIX + "#" + educationId;
    }

    public String getId() {
        return sk.split("#")[1];
    }
}
