package com.uzh.ase.dailygrind.userservice.user.repository.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "users")
@NoArgsConstructor
public class User {

    @DynamoDBHashKey
    private String id;

    @DynamoDBAttribute
    private String firstName;

    @DynamoDBAttribute
    private String lastName;

    @DynamoDBAttribute
    private String email;

}
