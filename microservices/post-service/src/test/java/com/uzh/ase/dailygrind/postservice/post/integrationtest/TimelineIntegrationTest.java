package com.uzh.ase.dailygrind.postservice.post.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.postservice.config.DynamoDBConfig;
import com.uzh.ase.dailygrind.postservice.post.config.AwsTestCredentialsConfig;
import com.uzh.ase.dailygrind.postservice.post.config.DynamoDBTestConfig;
import com.uzh.ase.dailygrind.postservice.post.config.LocalStackTestConfig;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.DailyPostEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.postservice.post.sqs.UserEventConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({LocalStackTestConfig.class, AwsTestCredentialsConfig.class, DynamoDBTestConfig.class, DynamoDBConfig.class})
public class TimelineIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserEventConsumer userEventPublisher() {
            return Mockito.mock(UserEventConsumer.class);
        }
    }

    @Autowired
    private DynamoDbTable<PostEntity> postTable;

    @Autowired
    private DynamoDbTable<UserEntity> userTable;

    @Autowired
    private DynamoDbTable<FriendEntity> friendTable;

    @Autowired
    private DynamoDbTable<DailyPostEntity> dailyPostTable;

    @AfterEach
    void tearDown() {
        postTable.scan().items().forEach(postTable::deleteItem);
        userTable.scan().items().forEach(userTable::deleteItem);
        friendTable.scan().items().forEach(friendTable::deleteItem);
        dailyPostTable.scan().items().forEach(dailyPostTable::deleteItem);
    }

    @Test
    @WithMockUser(username = "12345")
    void getMyTimeline() throws Exception {
        // Given
        UserEntity user = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .build();
        userTable.putItem(user);

        UserEntity user2 = UserEntity.builder()
            .pk(UserEntity.generatePK("11111"))
            .sk(UserEntity.generateSK())
            .build();
        userTable.putItem(user2);

        PostEntity post = PostEntity.builder()
            .pk(PostEntity.generatePK("11111"))
            .sk(PostEntity.generateSK("1"))
            .postTitle("Post Title")
            .build();
        postTable.putItem(post);

        DailyPostEntity dailyPost = new DailyPostEntity("11111", "1");
        dailyPostTable.putItem(dailyPost);

        FriendEntity friendEntity = FriendEntity.builder()
            .pk(FriendEntity.generatePK("12345"))
            .sk(FriendEntity.generateSK("11111"))
            .build();
        friendTable.putItem(friendEntity);

        // When
        mockMvc.perform(get("/users/me/timeline"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].post.postId").value("1"))
            .andExpect(jsonPath("$[0].user.userId").value("11111"));
    }

    @Test
    @WithMockUser(username = "12345")
    void getMyTimeline_friendHasNoDailyPost() throws Exception {
        // Given
        UserEntity user = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .build();
        userTable.putItem(user);

        UserEntity user2 = UserEntity.builder()
            .pk(UserEntity.generatePK("11111"))
            .sk(UserEntity.generateSK())
            .build();
        userTable.putItem(user2);

        FriendEntity friendEntity = FriendEntity.builder()
            .pk(FriendEntity.generatePK("12345"))
            .sk(FriendEntity.generateSK("11111"))
            .build();
        friendTable.putItem(friendEntity);

        // When
        mockMvc.perform(get("/users/me/timeline"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = "12345")
    void getMyTimeline_noFriend() throws Exception {
        // Given
        UserEntity user = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .build();
        userTable.putItem(user);

        // When
        mockMvc.perform(get("/users/me/timeline"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

}
