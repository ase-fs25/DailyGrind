package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @DynamoDBHashKey(attributeName = "user_id") // Hash key for the table
    private String userId; // The user's unique ID

    @DynamoDBAttribute(attributeName = "name") // Attribute for user's name
    private String name; // The user's name (this will be indexed in the global secondary index)
}