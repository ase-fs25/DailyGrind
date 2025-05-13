package com.uzh.ase.dailygrind.postservice.post.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.postservice.config.DynamoDBConfig;
import com.uzh.ase.dailygrind.postservice.post.config.LocalStackTestConfig;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({LocalStackTestConfig.class, DynamoDBConfig.class})
public class CommentIntegrationTest {

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
    private DynamoDbTable<CommentEntity> commentTable;

    @AfterEach
    void tearDown() {
        postTable.scan().items().forEach(postTable::deleteItem);
        commentTable.scan().items().forEach(commentTable::deleteItem);
        userTable.scan().items().forEach(userTable::deleteItem);
    }

    @Test
    @WithMockUser("12345")
    void testGetCommentsForPost() throws Exception {
        // Given
        UserEntity user = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .build();
        userTable.putItem(user);

        PostEntity post = PostEntity.builder()
            .pk(PostEntity.generatePK("12345"))
            .sk(PostEntity.generateSK("1"))
            .postTitle("Post Title")
            .build();
        postTable.putItem(post);

        CommentEntity comment = CommentEntity.builder()
            .pk(CommentEntity.generatePK("12345", "1"))
            .sk(CommentEntity.generateSK("12"))
            .build();
        commentTable.putItem(comment);

        // When + Then
        mockMvc.perform(get("/posts/1/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].comment.commentId").value("12"))
                .andExpect(jsonPath("$.[0].user.userId").value("12345"));
    }

    @Test
    @WithMockUser("12345")
    void testCommentPost() throws Exception {
        // Given
        UserEntity user = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .build();
        userTable.putItem(user);

        PostEntity post = PostEntity.builder()
            .pk(PostEntity.generatePK("12345"))
            .sk(PostEntity.generateSK("1"))
            .postTitle("Post Title")
            .commentCount(0L)
            .build();
        postTable.putItem(post);

        CommentEntity comment = CommentEntity.builder()
            .pk(CommentEntity.generatePK("12345", "1"))
            .sk(CommentEntity.generateSK("12"))
            .build();

        // When
        mockMvc.perform(post("/posts/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated());

        // Then
        mockMvc.perform(get("/posts/1/comments")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].comment.commentId").value("12"))
            .andExpect(jsonPath("$.[0].user.userId").value("12345"));

        mockMvc.perform(get("/posts/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.commentCount").value(1));
    }

    @Test
    @WithMockUser("12345")
    void testDeleteComment() throws Exception {
        // Given
        UserEntity user = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .build();
        userTable.putItem(user);

        PostEntity post = PostEntity.builder()
            .pk(PostEntity.generatePK("12345"))
            .sk(PostEntity.generateSK("1"))
            .postTitle("Post Title")
            .commentCount(1L)
            .build();
        postTable.putItem(post);

        CommentEntity comment = CommentEntity.builder()
            .pk(CommentEntity.generatePK("12345", "1"))
            .sk(CommentEntity.generateSK("12"))
            .build();
        commentTable.putItem(comment);

        // When
        mockMvc.perform(delete("/posts/1/comments/12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Then
        mockMvc.perform(get("/posts/1/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentCount").value(0));
    }

}
