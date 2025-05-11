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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({LocalStackTestConfig.class, AwsTestCredentialsConfig.class, DynamoDBTestConfig.class, DynamoDBConfig.class})
public class UserIntegrationTest {

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
                .andExpect(jsonPath("$.numberOfFriends").value("0"));
        }

//        @Test
//        @WithMockUser(username = "12345")
//        void getUserDetailsTest_friend() throws Exception {
//            // Given
//            UserEntity userEntity = UserEntity.builder()
//                .pk(UserEntity.generatePK("12345"))
//                .sk(UserEntity.generateSK())
//                .email("testuser@gmail.com")
//                .build();
//            userTable.putItem(userEntity);
//
//            UserEntity userEntity2 = UserEntity.builder()
//                .pk(UserEntity.generatePK("22222"))
//                .sk(UserEntity.generateSK())
//                .email("testuser2@gmail.com")
//                .build();
//            userTable.putItem(userEntity2);
//
//            friendRequestEntityDynamoDbTable.putItem();
//
//            // When + Then
//            mockMvc.perform(get("/users/12345/details")
//                    .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userInfo.userId").value("12345"))
//                .andExpect(jsonPath("$.userInfo.email").value("testuser@gmail.com"))
//                .andExpect(jsonPath("$.jobs").isEmpty())
//                .andExpect(jsonPath("$.educations").isEmpty());
//        }

    }

}
