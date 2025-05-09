package com.uzh.ase.dailygrind.postservice.post.repository.entity;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPostEntity {

    public static final String PK_PREFIX = "USER";
    public static final String PK_SUFFIX = "DAILY";
    public static final String SK_PREFIX = "POST";

    @Getter(onMethod_ =  {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;

    @Getter(onMethod_ =  {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;

    private long ttl;

    public DailyPostEntity(String userId, String postId, boolean ttlMinutes) {
        this.pk = generatePK(userId);
        this.sk = generateSK(postId);
        System.out.println(ttlMinutes);
        if (ttlMinutes) {
            this.ttl = Instant.now()
                .plus(Duration.ofMinutes(1))
                .getEpochSecond();
        } else {
            // Tomorrow 1pm
            // Tomorrow 1 PM UTC
            this.ttl = LocalDateTime.now(ZoneOffset.UTC)
                .plusDays(1)
                .withHour(13)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toEpochSecond(ZoneOffset.UTC);
        }
    }

    public static String generatePK(String userId) {
        return PK_PREFIX + "#" + userId + "#" + PK_SUFFIX;
    }

    public static String generateSK(String postId) {
        return SK_PREFIX + "#" + postId;
    }

    public String getId() {
        return sk.split("#")[1];
    }

}
