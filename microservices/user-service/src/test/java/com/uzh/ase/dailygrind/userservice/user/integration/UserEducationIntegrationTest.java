package com.uzh.ase.dailygrind.userservice.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.userservice.config.DynamoDBConfig;
import com.uzh.ase.dailygrind.userservice.user.config.AwsTestCredentialsConfig;
import com.uzh.ase.dailygrind.userservice.user.config.DynamoDBTestConfig;
import com.uzh.ase.dailygrind.userservice.user.config.LocalStackTestConfig;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
@Import({LocalStackTestConfig.class, AwsTestCredentialsConfig.class, DynamoDBTestConfig.class, DynamoDBConfig.class})
public class UserEducationIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DynamoDbTable<UserEntity> userTable;

    @Autowired
    private DynamoDbTable<UserEducationEntity> userEducationTable;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        userTable.scan().items().forEach(userTable::deleteItem);
        userEducationTable.scan().items().forEach(userEducationTable::deleteItem);
    }

    @Test
    @WithMockUser(username = "12345")
    void testGetUserEducation_noEducation() throws Exception {
        // Given
        UserEntity userEntity = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .email("testuser@gmail.com")
            .build();
        userTable.putItem(userEntity);

        // When
        mockMvc.perform(get("/users/me/education")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = "12345")
    void testCreateUserEducation() throws Exception {
        // Given
        UserEntity userEntity = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .email("testuser@gmail.com")
            .build();
        userTable.putItem(userEntity);

        UserEducationEntity userEducationEntity = UserEducationEntity.builder()
            .pk(UserEducationEntity.generatePK("12345"))
            .sk(UserEducationEntity.generateSK(null))
            .degree("Bachelor of Science")
            .fieldOfStudy("Computer Science")
            .institution("Test University")
            .educationStartDate("2020-01-01")
            .educationEndDate("2024-01-01")
            .build();

        // When
        mockMvc.perform(post("/users/me/education")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userEducationEntity)))
            .andExpect(status().isCreated());

        // Then
        mockMvc.perform(get("/users/me/education")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].degree").value("Bachelor of Science"));
    }

    @Test
    @WithMockUser(username = "12345")
    void testUpdateUserEducation() throws Exception {
        // Given
        UserEntity userEntity = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .email("testuser@gmail.com")
            .build();
        userTable.putItem(userEntity);

        UserEducationEntity userEducationEntity = UserEducationEntity.builder()
            .pk(UserEducationEntity.generatePK("12345"))
            .sk(UserEducationEntity.generateSK(null))
            .degree("Bachelor of Science")
            .fieldOfStudy("Computer Science")
            .institution("Test University")
            .educationStartDate("2020-01-01")
            .educationEndDate("2024-01-01")
            .build();
        userEducationTable.putItem(userEducationEntity);

        UserEducationEntity updatedUserEducationEntity = UserEducationEntity.builder()
            .pk(UserEducationEntity.generatePK("12345"))
            .sk(UserEducationEntity.generateSK(userEducationEntity.getId()))
            .degree("Bachelor of Science")
            .fieldOfStudy("Computer Science")
            .institution("New University")
            .educationStartDate("2020-01-01")
            .educationEndDate("2024-01-01")
            .build();

        // When
        mockMvc.perform(put("/users/me/education/" + userEducationEntity.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updatedUserEducationEntity)))
            .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/users/me/education")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$.[0].institution").value("New University"));
    }

    @Test
    @WithMockUser(username = "12345")
    void testDeleteUserEducation() throws Exception {
        // Given
        UserEntity userEntity = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .email("testuser@gmail.com")
            .build();
        userTable.putItem(userEntity);

        UserEducationEntity userEducationEntity = UserEducationEntity.builder()
            .pk(UserEducationEntity.generatePK("12345"))
            .sk(UserEducationEntity.generateSK(null))
            .degree("Bachelor of Science")
            .fieldOfStudy("Computer Science")
            .institution("Test University")
            .educationStartDate("2020-01-01")
            .educationEndDate("2024-01-01")
            .build();
        userEducationTable.putItem(userEducationEntity);

        // When
        mockMvc.perform(delete("/users/me/education/" + userEducationEntity.getId())
                .contentType("application/json"))
            .andExpect(status().isNoContent());

        // Then
        mockMvc.perform(get("/users/me/education")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = "12345")
    void testDeleteUserEducation_unknownEducation() throws Exception {
        // Given
        UserEntity userEntity = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .email("testuser@gmail.com")
            .build();
        userTable.putItem(userEntity);

        // When
        mockMvc.perform(delete("/users/me/education/unknown_education_id")
                .contentType("application/json"))
            .andExpect(status().isNoContent());

        // Then
        mockMvc.perform(get("/users/me/education")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

}
