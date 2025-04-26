package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.UUID;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJobEntity {

    public static final String PK_SUFFIX = "JOB";

    private String pk; // USER#<userID>#JOB
    private String sk; // JOB#<jobID>

    private String jobTitle;
    private String companyName;
    private String jobLocation;
    private String jobStartDate;
    private String jobEndDate;
    private String jobDescription;

    public static String generatePK(String userId) {
        return UserEntity.PK_PREFIX + "#" + userId + "#" + PK_SUFFIX;
    }

    public static String generateSK(String jobId) {
        if (jobId == null || jobId.isEmpty()) jobId = UUID.randomUUID().toString();
        return PK_SUFFIX + "#" + jobId;
    }

    public String getId() {
        return sk.split("#")[1];
    }
}
