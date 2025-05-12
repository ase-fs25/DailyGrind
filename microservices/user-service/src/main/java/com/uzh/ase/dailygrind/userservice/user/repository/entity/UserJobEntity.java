package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

/**
 * Represents a user's job information in the DynamoDB database.
 * <p>
 * This entity stores details about a user's job, including job title, company name,
 * job location, job start and end dates, and a description. It also includes the
 * partition key (PK) and sort key (SK) for DynamoDB, along with utility methods for key generation.
 * </p>
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJobEntity {

    // Constant for generating PK suffix for job entries
    public static final String PK_SUFFIX = "JOB";

    private String pk;
    private String sk;

    /**
     * Retrieves the partition key (PK) for the user job entity.
     *
     * @return the partition key (PK) for the user job entity.
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * Retrieves the sort key (SK) for the user job entity.
     *
     * @return the sort key (SK) for the user job entity.
     */
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    private String jobTitle;
    private String companyName;
    private String jobLocation;
    private String jobStartDate;
    private String jobEndDate;
    private String jobDescription;

    /**
     * Generates the partition key (PK) for a given user ID and job suffix.
     * The PK format is "USER#userId#JOB".
     *
     * @param userId the user ID to generate the PK.
     * @return the generated PK.
     */
    public static String generatePK(String userId) {
        return UserEntity.PK_PREFIX + "#" + userId + "#" + PK_SUFFIX;
    }

    /**
     * Generates the sort key (SK) for the user job entity.
     * The SK format is "JOB#jobId".
     * If no job ID is provided, a new random UUID will be generated.
     *
     * @param jobId the job ID to generate the SK.
     * @return the generated SK.
     */
    public static String generateSK(String jobId) {
        if (jobId == null || jobId.isEmpty()) jobId = UUID.randomUUID().toString();
        return PK_SUFFIX + "#" + jobId;
    }

    /**
     * Retrieves the job ID by parsing the sort key (SK).
     *
     * @return the job ID.
     */
    public String getId() {
        return sk.split("#")[1];
    }
}
