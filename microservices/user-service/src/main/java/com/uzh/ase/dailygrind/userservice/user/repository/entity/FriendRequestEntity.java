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
public class FriendRequestEntity {

    private String pk;
    private String sk;
    private String senderId;
    private String receiverId;
    private String status; // "PENDING", "ACCEPTED", "DECLINED", "CANCELLED"

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

    @DynamoDbAttribute("senderId")
    public String getSenderId() {
        return senderId;
    }

    @DynamoDbAttribute("receiverId")
    public String getReceiverId() {
        return receiverId;
    }

    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }
}
