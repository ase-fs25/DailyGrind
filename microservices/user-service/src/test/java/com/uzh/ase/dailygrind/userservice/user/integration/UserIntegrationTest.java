package com.uzh.ase.dailygrind.userservice.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.userservice.config.DynamoDBConfig;
import com.uzh.ase.dailygrind.userservice.user.config.DynamoDBTestConfig;
import com.uzh.ase.dailygrind.userservice.user.config.AwsTestCredentialsConfig;
import com.uzh.ase.dailygrind.userservice.user.config.LocalStackTestConfig;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserCreateDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.FriendshipEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import com.uzh.ase.dailygrind.userservice.user.sns.UserEventPublisher;
import com.uzh.ase.dailygrind.userservice.user.sns.events.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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


import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({LocalStackTestConfig.class, AwsTestCredentialsConfig.class, DynamoDBTestConfig.class, DynamoDBConfig.class})
public class UserIntegrationTest {

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
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DynamoDbTable<UserEntity> userTable;

    @Autowired
    private DynamoDbTable<UserJobEntity> userJobTable;

    @Autowired
    private DynamoDbTable<UserEducationEntity> userEducationTable;

    @Autowired
    private DynamoDbTable<FriendshipEntity> friendRequestEntityDynamoDbTable;

    @AfterEach
    void tearDown() {
        userTable.scan().items().forEach(userTable::deleteItem);
    }

    @Nested
    class GetAllUsersTest {

        @Test
        @WithMockUser(username = "12345")
        void getAllUsers_singleUserInDb() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        }

        @Test
        @WithMockUser(username = "12345")
        void getAllUsers_multipleUsersInDb() throws Exception {
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

            UserEntity userEntity3 = UserEntity.builder()
                .pk(UserEntity.generatePK("33333"))
                .sk(UserEntity.generateSK())
                .email("testuser3@gmail.com")
                .build();
            userTable.putItem(userEntity3);

            // When + Then
            mockMvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        }

    }

    @Nested
    class CreateUserTest {

        @Test
        @WithMockUser(username = "12345")
        void createUserTest_nullUsername() throws Exception {
            // Given
            UserCreateDto userCreateDto = new UserCreateDto(
                null,
                "testUser@gmail.com",
                "John",
                "Doe",
                "1990-01-01",
                "New York",
                "http://example.com/profile.jpg"
            );

            // When
            mockMvc.perform(post("/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isCreated());

            // Then
            mockMvc.perform(get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("12345"))
                .andExpect(jsonPath("$.email").value("testUser@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.birthday").value("1990-01-01"))
                .andExpect(jsonPath("$.location").value("New York"))
                .andExpect(jsonPath("$.profilePictureUrl").value("http://example.com/profile.jpg"));

            verify(userEventPublisher).publishUserEvent(
                eq(EventType.USER_CREATED),
                argThat(event -> event.userId().equals("12345"))
            );
        }

        @Test
        @WithMockUser(username = "54321")
        void createUserTest_userNameInDto() throws Exception {
            // Given
            UserCreateDto userCreateDto = new UserCreateDto(
                "12345",
                "testUser@gmail.com",
                "John",
                "Doe",
                "1990-01-01",
                "New York",
                "http://example.com/profile.jpg"
            );

            // When
            mockMvc.perform(post("/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isCreated());

            // Then
            mockMvc.perform(get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("54321")) // Always take user Id from token
                .andExpect(jsonPath("$.email").value("testUser@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.birthday").value("1990-01-01"))
                .andExpect(jsonPath("$.location").value("New York"))
                .andExpect(jsonPath("$.profilePictureUrl").value("http://example.com/profile.jpg"));

            verify(userEventPublisher).publishUserEvent(
                eq(EventType.USER_CREATED),
                argThat(event -> event.userId().equals("54321"))
            );
        }
    }

    @Nested
    class UpdateUserTest {

        @Test
        @WithMockUser(username = "54321")
        void updateUserTest_userInDb() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("54321"))
                .sk(UserEntity.generateSK())
                .email("testUser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserCreateDto userCreateDto = new UserCreateDto(
                "54321",
                "testUser2@gmail.com",
                "John",
                "Doe",
                "1990-01-01",
                "New York",
                "http://example.com/profile.jpg"
            );

            // When
            mockMvc.perform(put("/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk());

            // Then
            mockMvc.perform(get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("54321"))
                .andExpect(jsonPath("$.email").value("testUser2@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.birthday").value("1990-01-01"))
                .andExpect(jsonPath("$.location").value("New York"))
                .andExpect(jsonPath("$.profilePictureUrl").value("http://example.com/profile.jpg"));

            verify(userEventPublisher).publishUserEvent(
                eq(EventType.USER_UPDATED),
                argThat(event -> event.userId().equals("54321"))
            );
        }
    }

    @Nested
    class DeleteUserTest {

        @Test
        @WithMockUser(username = "54321")
        void deleteUserTest_userInDb() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("54321"))
                .sk(UserEntity.generateSK())
                .email("testUser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            // When
            mockMvc.perform(delete("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

            // Then
            mockMvc.perform(get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

            verify(userEventPublisher).publishUserEvent(
                eq(EventType.USER_DELETED),
                argThat(event -> event.userId().equals("54321"))
            );
        }

        @Test
        @WithMockUser(username = "54321")
        void deleteUserTest_userNotInDb() throws Exception {
            // Given

            // When
            mockMvc.perform(delete("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

            // Then
            mockMvc.perform(get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

            verifyNoInteractions(userEventPublisher);
        }

    }

    @Nested
    class GetMyUserInfoTest {

        @Test
        @WithMockUser(username = "12345")
        void getMyUserInfoTest() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("12345"))
                .andExpect(jsonPath("$.email").value("testuser@gmail.com"));
        }

        @Test
        @WithMockUser(username = "12345")
        void getMyUserInfoTest_noUser() throws Exception {
            mockMvc.perform(get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        }

    }

    @Nested
    class GetUserDetailsTest {

        @Test
        @WithMockUser(username = "12345")
        void getUserDetailsTest_jobAndEducation() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            UserJobEntity userJobEntity = UserJobEntity.builder()
                .pk(UserJobEntity.generatePK("12345"))
                .sk(UserJobEntity.generateSK(null))
                .jobTitle("Software Engineer")
                .companyName("Tech Company")
                .build();
            userJobTable.putItem(userJobEntity);

            UserEducationEntity userEducationEntity = UserEducationEntity.builder()
                .pk(UserEducationEntity.generatePK("12345"))
                .sk(UserEducationEntity.generateSK(null))
                .degree("Bachelor's")
                .institution("Tech University")
                .build();
            userEducationTable.putItem(userEducationEntity);

            // When + Then
            mockMvc.perform(get("/users/12345/details")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userInfo.userId").value("12345"))
                .andExpect(jsonPath("$.userInfo.email").value("testuser@gmail.com"))
                .andExpect(jsonPath("$.userInfo.numberOfFriends").value("0"))
                .andExpect(jsonPath("$.jobs[0].jobTitle").value("Software Engineer"))
                .andExpect(jsonPath("$.jobs[0].companyName").value("Tech Company"))
                .andExpect(jsonPath("$.educations[0].degree").value("Bachelor's"))
                .andExpect(jsonPath("$.educations[0].institution").value("Tech University"));
        }

        @Test
        @WithMockUser(username = "12345")
        void getUserDetailsTest_noJobAndEducation() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(get("/users/12345/details")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userInfo.userId").value("12345"))
                .andExpect(jsonPath("$.userInfo.email").value("testuser@gmail.com"))
                .andExpect(jsonPath("$.jobs").isEmpty())
                .andExpect(jsonPath("$.educations").isEmpty())
                .andExpect(jsonPath("$.userInfo.numberOfFriends").value("0"));
        }

        @Test
        @WithMockUser(username = "12345")
        void getUserDetailsTest_friend() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .numFriends(1)
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("22222"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .numFriends(1)
                .build();
            userTable.putItem(userEntity2);

            friendRequestEntityDynamoDbTable.putItem(
                FriendshipEntity.builder()
                    .pk(FriendshipEntity.generatePK("12345"))
                    .sk(FriendshipEntity.generateSK("22222"))
                    .friendshipAccepted(true)
                    .incoming(false)
                    .build()
            );
            friendRequestEntityDynamoDbTable.putItem(
                FriendshipEntity.builder()
                    .pk(FriendshipEntity.generatePK("22222"))
                    .sk(FriendshipEntity.generateSK("12345"))
                    .friendshipAccepted(true)
                    .incoming(true)
                    .build()
            );

            // When + Then
            mockMvc.perform(get("/users/22222/details")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userInfo.userId").value("22222"))
                .andExpect(jsonPath("$.userInfo.email").value("testuser2@gmail.com"))
                .andExpect(jsonPath("$.userInfo.isFriend").value(true))
                .andExpect(jsonPath("$.jobs").isEmpty())
                .andExpect(jsonPath("$.userInfo.numberOfFriends").value("1"))
                .andExpect(jsonPath("$.educations").isEmpty());
        }

    }

    @Nested
    class SearchUsersTest {

        @Test
        @WithMockUser(username = "12345")
        void searchUsersTest_firstName() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(get("/users/search")
                    .param("name", "John")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("12345"));
        }

        @Test
        @WithMockUser(username = "12345")
        void searchUsersTest_firstNamePart() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(get("/users/search")
                    .param("name", "Jo")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("12345"));
        }

        @Test
        @WithMockUser(username = "12345")
        void searchUsersTest_lastName() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(get("/users/search")
                    .param("name", "Doe")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("12345"));
        }

        @Test
        @WithMockUser(username = "12345")
        void searchUsersTest_lastNamePart() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .build();
            userTable.putItem(userEntity);

            // When + Then
            mockMvc.perform(get("/users/search")
                    .param("name", "Do")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("12345"));
        }

        @Test
        @WithMockUser(username = "12345")
        void searchUsersTest_firstNameAndLastName() throws Exception {
            // Given
            UserEntity userEntity = UserEntity.builder()
                .pk(UserEntity.generatePK("12345"))
                .sk(UserEntity.generateSK())
                .email("testuser@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .build();
            userTable.putItem(userEntity);

            UserEntity userEntity2 = UserEntity.builder()
                .pk(UserEntity.generatePK("12345655"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .firstName("Peter")
                .lastName("Doe")
                .build();
            userTable.putItem(userEntity2);

            UserEntity userEntity3 = UserEntity.builder()
                .pk(UserEntity.generatePK("3334564"))
                .sk(UserEntity.generateSK())
                .email("testuser2@gmail.com")
                .firstName("John")
                .lastName("Smith")
                .build();
            userTable.putItem(userEntity3);

            // When + Then
            mockMvc.perform(get("/users/search")
                    .param("name", "John Doe")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("12345"));
        }
    }

}
