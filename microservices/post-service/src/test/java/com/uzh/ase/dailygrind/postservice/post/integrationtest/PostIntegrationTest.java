package com.uzh.ase.dailygrind.postservice.post.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.postservice.config.DynamoDBConfig;
import com.uzh.ase.dailygrind.postservice.post.config.AwsTestCredentialsConfig;
import com.uzh.ase.dailygrind.postservice.post.config.DynamoDBTestConfig;
import com.uzh.ase.dailygrind.postservice.post.config.LocalStackTestConfig;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.*;
import com.uzh.ase.dailygrind.postservice.post.sqs.UserEventConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({LocalStackTestConfig.class, AwsTestCredentialsConfig.class, DynamoDBTestConfig.class, DynamoDBConfig.class})
public class PostIntegrationTest {

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
    private DynamoDbTable<DailyPostEntity> dailyPostTable;

    @Autowired
    private DynamoDbTable<PinnedPostEntity> pinnedPostTable;

    @Autowired
    private DynamoDbTable<CommentEntity> commentTable;

    @Autowired
    private DynamoDbTable<LikeEntity> likeTable;

    @Autowired
    private DynamoDbTable<UserEntity> userTable;

    @AfterEach
    void tearDown() {
        postTable.scan().items().forEach(postTable::deleteItem);
        dailyPostTable.scan().items().forEach(dailyPostTable::deleteItem);
        pinnedPostTable.scan().items().forEach(pinnedPostTable::deleteItem);
        likeTable.scan().items().forEach(likeTable::deleteItem);
        commentTable.scan().items().forEach(commentTable::deleteItem);
        userTable.scan().items().forEach(userTable::deleteItem);
    }

    @Nested
    class GetDailyPost {

        @Test
        @WithMockUser(username = "12345")
        void testGetDailyPost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .postTitle("Test Post")
                .commentCount(0L)
                .likeCount(0L)
                .build();
            postTable.putItem(postEntity);

            DailyPostEntity dailyPostEntity = DailyPostEntity.builder()
                .pk(DailyPostEntity.generatePK("12345"))
                .sk(DailyPostEntity.generateSK("1"))
                .build();
            dailyPostTable.putItem(dailyPostEntity);

            // When
            mockMvc.perform(get("/users/me/daily-post")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value("1"))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0));
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetDailyPost_noDailyPost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .postTitle("Test Post")
                .commentCount(0L)
                .likeCount(0L)
                .build();
            postTable.putItem(postEntity);

            // When
            mockMvc.perform(get("/users/me/daily-post")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetDailyPost_ofOtherUser() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("77777"))
                .sk(PostEntity.generateSK("1"))
                .postTitle("Test Post")
                .commentCount(0L)
                .likeCount(0L)
                .build();
            postTable.putItem(postEntity);

            DailyPostEntity dailyPostEntity = DailyPostEntity.builder()
                .pk(DailyPostEntity.generatePK("77777"))
                .sk(DailyPostEntity.generateSK("1"))
                .build();
            dailyPostTable.putItem(dailyPostEntity);

            // When
            mockMvc.perform(get("/users/77777/daily-post")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value("1"))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0));
        }

    }

    @Nested
    class GetPosts {

        @Test
        @WithMockUser(username = "12345")
        void testGetPosts() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .postTitle("Test Post")
                .commentCount(0L)
                .likeCount(0L)
                .build();
            postTable.putItem(postEntity);

            // When
            mockMvc.perform(get("/users/me/posts")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value("1"))
                .andExpect(jsonPath("$[0].title").value("Test Post"))
                .andExpect(jsonPath("$[0].content").isEmpty())
                .andExpect(jsonPath("$[0].likeCount").value(0))
                .andExpect(jsonPath("$[0].commentCount").value(0));
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetPosts_noPosts() throws Exception {
            // When
            mockMvc.perform(get("/users/me/posts")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetPosts_ofOtherUser() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("77777"))
                .sk(PostEntity.generateSK("1"))
                .postTitle("Test Post")
                .commentCount(0L)
                .likeCount(0L)
                .build();
            postTable.putItem(postEntity);

            // When
            mockMvc.perform(get("/users/77777/posts")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value("1"))
                .andExpect(jsonPath("$[0].title").value("Test Post"))
                .andExpect(jsonPath("$[0].content").isEmpty())
                .andExpect(jsonPath("$[0].likeCount").value(0))
                .andExpect(jsonPath("$[0].commentCount").value(0));
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetPostById() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .postTitle("Test Post")
                .commentCount(0L)
                .likeCount(0L)
                .build();
            postTable.putItem(postEntity);

            // When
            mockMvc.perform(get("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value("1"))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0));
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetPostById_requesterHasLikedPost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .postTitle("Test Post")
                .commentCount(0L)
                .likeCount(1L)
                .build();
            postTable.putItem(postEntity);

            LikeEntity likeEntity = LikeEntity.builder()
                .pk(LikeEntity.generatePK("1"))
                .sk(LikeEntity.generateSK("12345"))
                .build();
            likeTable.putItem(likeEntity);

            // When
            mockMvc.perform(get("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value("1"))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.likeCount").value(1))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.commentCount").value(0));
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetPostById_requesterHasPinnedPost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .postTitle("Test Post")
                .commentCount(0L)
                .likeCount(0L)
                .build();
            postTable.putItem(postEntity);

            PinnedPostEntity pinnedPostEntity = new PinnedPostEntity("12345", postEntity.getPostId());
            pinnedPostTable.putItem(pinnedPostEntity);

            // When
            mockMvc.perform(get("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value("1"))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.isPinned").value(true))
                .andExpect(jsonPath("$.commentCount").value(0));
        }
    }

    @Nested
    class CreatePost {

        @Test
        @WithMockUser(username = "12345")
        void testCreatePost() throws Exception {
            // Given
            PostDto postDto = new PostDto("1", "Test Post", "Test Content", null, 0L, 0L, false, false);

            // When
            mockMvc.perform(post("/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0));

            // Then
            mockMvc.perform(get("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value("1"))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0));
        }

    }

    @Nested
    class UpdatePost {

        @Test
        @WithMockUser(username = "12345")
        void testUpdatePost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .build();
            postTable.putItem(postEntity);

            PostDto postDto = new PostDto("1", "Test Post", "Test Content", null, 0L, 0L, false, false);

            // When
            mockMvc.perform(put("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0));

            // Then
            mockMvc.perform(get("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value("1"))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0));
        }

        @Test
        @WithMockUser(username = "12345")
        void testUpdatePost_postDoesNotExist() throws Exception {
            // Given
            PostDto postDto = new PostDto("1", "Test Post", "Test Content", null, 0L, 0L, false, false);

            // When + Then
            mockMvc.perform(put("/posts/unknown_post_id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class DeletePost {

        @Test
        @WithMockUser(username = "12345")
        void testDeletePost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .build();
            postTable.putItem(postEntity);

            // When
            mockMvc.perform(delete("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

            // Then
            mockMvc.perform(get("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        }

        @Test
        @WithMockUser
        void testDeletePost_postDoesNotExist() throws Exception {
            // When + Then
            mockMvc.perform(delete("/posts/unknown_post_id")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(username = "12345")
        void testDeletePost_isDailyPost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .build();
            postTable.putItem(postEntity);

            DailyPostEntity dailyPostEntity = new DailyPostEntity("12345", "1");
            dailyPostTable.putItem(dailyPostEntity);

            // When
            mockMvc.perform(delete("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

            // Then
            mockMvc.perform(get("/users/me/daily-post"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        }

        @Test
        @WithMockUser(username = "12345")
        void testDeletePost_isPinnedPost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .build();
            postTable.putItem(postEntity);

            PinnedPostEntity pinnedPostEntity = new PinnedPostEntity("12345", "1");
            pinnedPostTable.putItem(pinnedPostEntity);

            // When
            mockMvc.perform(delete("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

            // Then
            mockMvc.perform(get("/users/me/pinned-posts"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
        }

        @Test
        @WithMockUser(username = "12345")
        void testDeletePost_deleteCommentsAndLikes() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .build();
            userTable.putItem(userEntity);

            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .build();
            postTable.putItem(postEntity);

            CommentEntity commentEntity = CommentEntity.builder()
                .pk(CommentEntity.generatePK("12345", "1"))
                .sk(CommentEntity.generateSK("99"))
                .commentContent("Test Comment")
                .build();
            commentTable.putItem(commentEntity);

            LikeEntity likeEntity = LikeEntity.builder()
                .pk(LikeEntity.generatePK("1"))
                .sk(LikeEntity.generateSK("12345"))
                .build();
            likeTable.putItem(likeEntity);

            // When
            mockMvc.perform(delete("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

            // Then
            mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

            mockMvc.perform(get("/posts/1/comments"))
                .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class LikePost {

        @Test
        @WithMockUser(username = "12345")
        void testLikePost_postDoesNotExist() throws Exception {
            // When + Then
            mockMvc.perform(post("/posts/non_existent_post/likes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser("12345")
        void testUnlikePost_postDoesNotExist() throws Exception {
            // When + Then
            mockMvc.perform(delete("/posts/non_existent_post/likes")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        void testLikePost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .likeCount(0L)
                .build();
            postTable.putItem(postEntity);

            // When
            mockMvc.perform(post("/posts/1/likes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

            // Then
            mockMvc.perform(get("/posts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.likeCount").value(1))
                .andExpect(jsonPath("$.isLiked").value(true));
        }

        @Test
        @WithMockUser(username = "12345")
        void testUnlikePost() throws Exception {
            // Given
            PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.generatePK("12345"))
                .sk(PostEntity.generateSK("1"))
                .likeCount(1L)
                .build();
            postTable.putItem(postEntity);

            LikeEntity likeEntity = LikeEntity.builder()
                .pk(LikeEntity.generatePK("1"))
                .sk(LikeEntity.generateSK("12345"))
                .build();
            likeTable.putItem(likeEntity);

            // When
            mockMvc.perform(delete("/posts/1/likes")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

            // Then
            mockMvc.perform(get("/posts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isLiked").value(false))
                .andExpect(jsonPath("$.likeCount").value(0));

        }

    }

}
