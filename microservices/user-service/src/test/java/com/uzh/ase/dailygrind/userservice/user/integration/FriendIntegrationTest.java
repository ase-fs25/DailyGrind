package com.uzh.ase.dailygrind.userservice.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.userservice.config.DynamoDBConfig;
import com.uzh.ase.dailygrind.userservice.user.config.AwsTestCredentialsConfig;
import com.uzh.ase.dailygrind.userservice.user.config.DynamoDBTestConfig;
import com.uzh.ase.dailygrind.userservice.user.config.LocalStackTestConfig;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFriendRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.FriendshipEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.sns.UserEventPublisher;
import com.uzh.ase.dailygrind.userservice.user.sns.events.EventType;
import com.uzh.ase.dailygrind.userservice.user.sns.events.FriendshipEvent;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({LocalStackTestConfig.class, AwsTestCredentialsConfig.class, DynamoDBTestConfig.class, DynamoDBConfig.class})
public class FriendIntegrationTest {

    @Autowired
    private UserEventPublisher userEventPublisher;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserEventPublisher userEventPublisher() {
            return Mockito.mock(UserEventPublisher.class);
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DynamoDbTable<UserEntity> userTable;

    @Autowired
    private DynamoDbTable<FriendshipEntity> friendRequestEntityDynamoDbTable;

    @Autowired
    private UserFriendRepository userFriendRepository;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        userTable.scan().items().forEach(userTable::deleteItem);
        friendRequestEntityDynamoDbTable.scan().items().forEach(friendRequestEntityDynamoDbTable::deleteItem);
        Mockito.clearInvocations(userEventPublisher);
    }

    @Nested
    class SendFriendRequestTests {

        @Test
        @WithMockUser(username = "12345")
        void testSendFriendRequest_toOtherUser() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            // When
            mockMvc.perform(post("/users/requests")
                    .param("targetUserId", "22222")
                    .contentType("application/json"))
                .andExpect(status().isOk());

            // Then
            List<String> userIdsOfIncomingRequests = userFriendRepository.findUserIdsOfIncomingRequests("22222");
            assertThat(userIdsOfIncomingRequests).contains("12345");
            List<String> userIdsOfOutgoingRequests = userFriendRepository.findUserIdsOfOutgoingRequests("12345");
            assertThat(userIdsOfOutgoingRequests).contains("22222");
        }

        @Test
        @WithMockUser(username = "12345")
        void testSendFriendRequest_toMySelf() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(post("/users/requests")
                .param("targetUserId", "12345")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "12345")
        void testSendFriendRequest_userDoesNotExist() throws Exception {
            // When + Then
            mockMvc.perform(post("/users/requests")
                    .param("targetUserId", "unknownUser")
                    .contentType("application/json"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "12345")
        void testSendFriendRequest_alreadyFriends() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("12345", "22222");
            userFriendRepository.acceptFriendRequest("12345", "22222");

            // When + Then
            mockMvc.perform(post("/users/requests")
                    .param("targetUserId", "22222")
                    .contentType("application/json"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "12345")
        void testSendFriendRequest_alreadySent() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("12345", "22222");

            // When + Then
            mockMvc.perform(post("/users/requests")
                    .param("targetUserId", "22222")
                    .contentType("application/json"))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class AcceptFriendRequestsTests {

        @Test
        @WithMockUser(username = "12345")
        void testAcceptFriendReqeust() throws Exception {
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("22222", "12345");

            // When + Then
            mockMvc.perform(post("/users/requests/22222/accept")
                    .contentType("application/json"))
                .andExpect(status().isOk());

            List<String> user1friendIds = userFriendRepository.findFriends("12345");
            assertThat(user1friendIds).contains("22222");

            List<String> user2friendIds = userFriendRepository.findFriends("22222");
            assertThat(user2friendIds).contains("12345");

            verify(userEventPublisher).publishFriendshipEvent(
                EventType.FRIENDSHIP_CREATED,
                new FriendshipEvent("22222", "12345")
            );
        }

        @Test
        @WithMockUser(username = "12345")
        void testAcceptFriendReqeust_noFriendRequest() throws Exception {
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            // When + Then
            mockMvc.perform(post("/users/requests/22222/accept")
                    .contentType("application/json"))
                .andExpect(status().isBadRequest());

            verifyNoInteractions(userEventPublisher);
        }

    }

    @Nested
    class RejectFriendRequestsTests {

        @Test
        @WithMockUser(username = "12345")
        void testRejectFriendReqeust() throws Exception {
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("22222", "12345");

            // When + Then
            mockMvc.perform(delete("/users/requests/22222/decline")
                    .contentType("application/json"))
                .andExpect(status().isOk());

            List<String> user1friendIds = userFriendRepository.findFriends("12345");
            assertThat(user1friendIds).isEmpty();

            List<String> user2friendIds = userFriendRepository.findFriends("22222");
            assertThat(user2friendIds).isEmpty();
        }

        @Test
        @WithMockUser(username = "12345")
        void testRejectFriendReqeust_noFriendRequest() throws Exception {
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            // When + Then
            mockMvc.perform(delete("/users/requests/22222/decline")
                    .contentType("application/json"))
                .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class CancelFriendRequestsTests {

        @Test
        @WithMockUser(username = "12345")
        void testCancelFriendReqeust() throws Exception {
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("22222", "12345");

            // When + Then
            mockMvc.perform(delete("/users/requests/22222/cancel")
                    .contentType("application/json"))
                .andExpect(status().isOk());

            List<String> user1friendIds = userFriendRepository.findFriends("12345");
            assertThat(user1friendIds).isEmpty();

            List<String> user2friendIds = userFriendRepository.findFriends("22222");
            assertThat(user2friendIds).isEmpty();
        }

        @Test
        @WithMockUser(username = "12345")
        void testCancelFriendReqeust_noFriendRequest() throws Exception {
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            // When + Then
            mockMvc.perform(delete("/users/requests/22222/cancel")
                    .contentType("application/json"))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetOutgoingFriendRequestsTests {

        @Test
        @WithMockUser(username = "12345")
        void testGetOutgoingFriendRequests() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("12345", "22222");

            // When + Then
            mockMvc.perform(get("/users/requests/outgoing")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] outgoingRequests = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(outgoingRequests[0].userId()).isEqualTo("22222");
                });
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetOutgoingFriendRequests_noRequests() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(get("/users/requests/outgoing")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] outgoingRequests = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(outgoingRequests).isEmpty();
                });
        }
    }

    @Nested
    class GetIncomingFriendRequestsTests {

        @Test
        @WithMockUser(username = "12345")
        void testGetOutgoingFriendRequests() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("22222", "12345");

            // When + Then
            mockMvc.perform(get("/users/requests/incoming")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] outgoingRequests = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(outgoingRequests[0].userId()).isEqualTo("22222");
                });
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetOutgoingFriendRequests_noRequests() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(get("/users/requests/incoming")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] outgoingRequests = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(outgoingRequests).isEmpty();
                });
        }
    }

    @Nested
    class GetFriendsTests {

        @Test
        @WithMockUser(username = "12345")
        void testGetFriends() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("22222", "12345");
            userFriendRepository.acceptFriendRequest("22222", "12345");

            // When + Then
            mockMvc.perform(get("/users/me/friends")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] friends = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(friends[0].userId()).isEqualTo("22222");
                });

            mockMvc.perform(get("/users/22222/friends")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] friends = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(friends[0].userId()).isEqualTo("12345");
                });
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetFriends_noFriends() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            // When + Then
            mockMvc.perform(get("/users/me/friends")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] friends = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(friends).isEmpty();
                });

            mockMvc.perform(get("/users/22222/friends")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] friends = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(friends).isEmpty();
                });
        }

        @Test
        @WithMockUser(username = "12345")
        void testGetFriends_pendingRequest() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("22222", "12345");

            // When + Then
            mockMvc.perform(get("/users/me/friends")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] friends = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(friends).isEmpty();
                });

            mockMvc.perform(get("/users/22222/friends")
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    UserInfoDto[] friends = objectMapper.readValue(content, UserInfoDto[].class);
                    assertThat(friends).isEmpty();
                });
        }

    }

    @Nested
    class RemoveFriendTests {

        @Test
        @WithMockUser(username = "12345")
        void testRemoveFriend() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            userFriendRepository.createFriendRequest("22222", "12345");
            userFriendRepository.acceptFriendRequest("22222", "12345");

            // When + Then
            mockMvc.perform(delete("/users/me/friends/22222/remove")
                    .contentType("application/json"))
                .andExpect(status().isOk());

            List<String> user1friendIds = userFriendRepository.findFriends("12345");
            assertThat(user1friendIds).isEmpty();

            List<String> user2friendIds = userFriendRepository.findFriends("22222");
            assertThat(user2friendIds).isEmpty();

            verify(userEventPublisher).publishFriendshipEvent(
                EventType.FRIENDSHIP_DELETED,
                new FriendshipEvent("12345", "22222")
            );
        }

        @Test
        @WithMockUser(username = "12345")
        void testRemoveFriend_notFriends() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .build();
            userTable.putItem(userEntity2);

            // When + Then
            mockMvc.perform(delete("/users/me/friends/22222/remove")
                    .contentType("application/json"))
                .andExpect(status().isOk());

            List<String> user1friendIds = userFriendRepository.findFriends("12345");
            assertThat(user1friendIds).isEmpty();

            List<String> user2friendIds = userFriendRepository.findFriends("22222");
            assertThat(user2friendIds).isEmpty();

            verifyNoInteractions(userEventPublisher);
        }
    }

}
