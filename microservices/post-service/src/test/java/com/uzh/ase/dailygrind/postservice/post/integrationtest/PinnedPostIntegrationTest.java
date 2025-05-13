package com.uzh.ase.dailygrind.postservice.post.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.postservice.config.DynamoDBConfig;
import com.uzh.ase.dailygrind.postservice.post.config.LocalStackTestConfig;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PinnedPostEntity;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({LocalStackTestConfig.class, DynamoDBConfig.class})
public class PinnedPostIntegrationTest {

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
    private ObjectMapper objectMapper;

    @Autowired
    private DynamoDbTable<PostEntity> postTable;

    @Autowired
    private DynamoDbTable<UserEntity> userTable;

    @Autowired
    private DynamoDbTable<PinnedPostEntity> pinnedPostTable;

    @AfterEach
    void tearDown() {
        postTable.scan().items().forEach(postTable::deleteItem);
        userTable.scan().items().forEach(userTable::deleteItem);
        pinnedPostTable.scan().items().forEach(pinnedPostTable::deleteItem);
    }

    @Test
    @WithMockUser(username = "12345")
    void testPinPost() throws Exception {
        // Given
        PostEntity post = PostEntity.builder()
            .pk(PostEntity.generatePK("12344"))
            .sk(PostEntity.generateSK("11111"))
            .postTitle("Post Title")
            .build();
        postTable.putItem(post);

        // When
        mockMvc.perform(post("/posts/users/me/pinned-posts/11111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/posts/users/me/pinned-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value("11111"));
    }

    @Test
    @WithMockUser(username = "12345")
    void testDeletePinnedPost() throws Exception {
        // Given
        PostEntity post = PostEntity.builder()
            .pk(PostEntity.generatePK("12345"))
            .sk(PostEntity.generateSK("11111"))
            .postTitle("Post Title")
            .build();
        postTable.putItem(post);

        PinnedPostEntity pinnedPost = new PinnedPostEntity("12345", "11111");
        pinnedPostTable.putItem(pinnedPost);

        // When
        mockMvc.perform(post("/posts/users/me/pinned-posts/11111")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/posts/users/me/pinned-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value("11111"));
    }

    @Test
    @WithMockUser(username = "12345")
    void testGetPinnedPosts() throws Exception {
        // Given
        PostEntity post = PostEntity.builder()
            .pk(PostEntity.generatePK("12345"))
            .sk(PostEntity.generateSK("11111"))
            .postTitle("Post Title")
            .build();
        postTable.putItem(post);

        PinnedPostEntity pinnedPost = new PinnedPostEntity("12345", "11111");
        pinnedPostTable.putItem(pinnedPost);

        // When + Then
        mockMvc.perform(get("/posts/users/me/pinned-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value("11111"));
    }

    @Test
    @WithMockUser(username = "12345")
    void testGetPinnedPostsEmpty() throws Exception {
        // When + Then
        mockMvc.perform(get("/posts/users/me/pinned-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "99999")
    void testGetPinnedPostsByUserId() throws Exception {
        // Given
        PostEntity post = PostEntity.builder()
            .pk(PostEntity.generatePK("12345"))
            .sk(PostEntity.generateSK("11111"))
            .postTitle("Post Title")
            .build();
        postTable.putItem(post);

        PinnedPostEntity pinnedPost = new PinnedPostEntity("12345", "11111");
        pinnedPostTable.putItem(pinnedPost);

        // When + Then
        mockMvc.perform(get("/posts/users/12345/pinned-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value("11111"));
    }

}
