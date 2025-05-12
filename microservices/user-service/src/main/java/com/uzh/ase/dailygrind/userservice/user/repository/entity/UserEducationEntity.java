package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

/**
 * Represents a user's education record in the DynamoDB database.
 * <p>
 * This entity stores information about a user's education, including the institution, degree,
 * field of study, start and end dates, and description. It also includes the partition key (PK) and sort key (SK)
 * for DynamoDB and utility methods for key generation.
 * </p>
 */
@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEducationEntity {

    // Constants for generating PK and SK
    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "EDUCATION";

    private String pk;
    private String sk;

    /**
     * Retrieves the partition key (PK) for the user education entity.
     *
     * @return the partition key (PK) for the user education entity.
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    /**
     * Retrieves the sort key (SK) for the user education entity.
     *
     * @return the sort key (SK) for the user education entity.
     */
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

    /**
     * Generates the partition key (PK) for a given user ID.
     * The PK format is "USER#<userId>#EDUCATION".
     *
     * @param id the user ID to generate the PK.
     * @return the generated PK.
     */
    public static String generatePK(String id) {
        return PK_PREFIX + "#" + id + "#" + PK_SUFFIX;
    }

    /**
     * Generates the sort key (SK) for a given education ID.
     * The SK format is "EDUCATION#<educationId>". If the educationId is null or empty, a random UUID is generated.
     *
     * @param educationId the education ID to generate the SK.
     * @return the generated SK.
     */
    public static String generateSK(String educationId) {
        if (educationId == null || educationId.isEmpty()) educationId = UUID.randomUUID().toString();
        return PK_SUFFIX + "#" + educationId;
    }

    /**
     * Retrieves the education ID by parsing the sort key (SK).
     *
     * @return the education ID.
     */
    public String getId() {
        return sk.split("#")[1];
    }
}
