package com.uzh.ase.dailygrind.userservice.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzh.ase.dailygrind.userservice.config.DynamoDBConfig;
import com.uzh.ase.dailygrind.userservice.user.config.AwsTestCredentialsConfig;
import com.uzh.ase.dailygrind.userservice.user.config.DynamoDBTestConfig;
import com.uzh.ase.dailygrind.userservice.user.config.LocalStackTestConfig;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({LocalStackTestConfig.class, AwsTestCredentialsConfig.class, DynamoDBTestConfig.class, DynamoDBConfig.class})
public class UserJobIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DynamoDbTable<UserEntity> userTable;

    @Autowired
    private DynamoDbTable<UserJobEntity> userJobTable;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        userTable.scan().items().forEach(userTable::deleteItem);
        userJobTable.scan().items().forEach(userJobTable::deleteItem);
    }

    @Test
    @WithMockUser(username = "12345")
    void testGetUserJobs_noJob() throws Exception {
        // Given
        UserEntity userEntity = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .email("testuser@gmail.com")
            .build();
        userTable.putItem(userEntity);

        // When
        mockMvc.perform(get("/users/me/jobs")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = "12345")
    void testCreateUserJob() throws Exception {
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
            .companyName("Test Company")
            .build();

        // When
        mockMvc.perform(post("/users/me/jobs")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userJobEntity)))
            .andExpect(status().isCreated());

        // Then
        mockMvc.perform(get("/users/me/jobs")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].jobTitle").value("Software Engineer"));
    }

    @Test
    @WithMockUser(username = "12345")
    void testUpdateUserJob() throws Exception {
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
            .companyName("Test Company")
            .build();
        userJobTable.putItem(userJobEntity);

        UserJobEntity updatedUserJobEntity = UserJobEntity.builder()
            .pk(UserJobEntity.generatePK("12345"))
            .sk(userJobEntity.getSk())
            .jobTitle("Senior Software Engineer")
            .companyName("Test Company")
            .build();

        // When
        mockMvc.perform(put("/users/me/jobs/" + userJobEntity.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updatedUserJobEntity)))
            .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/users/me/jobs")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$.[0].jobTitle").value("Senior Software Engineer"));
    }

    @Test
    @WithMockUser(username = "12345")
    void testDeleteUserJob() throws Exception {
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
            .companyName("Test Company")
            .build();
        userJobTable.putItem(userJobEntity);

        // When
        mockMvc.perform(delete("/users/me/jobs/" + userJobEntity.getId())
                .contentType("application/json"))
            .andExpect(status().isNoContent());

        // Then
        mockMvc.perform(get("/users/me/jobs")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(username = "12345")
    void testDeleteUserJob_unknownJob() throws Exception {
        // Given
        UserEntity userEntity = UserEntity.builder()
            .pk(UserEntity.generatePK("12345"))
            .sk(UserEntity.generateSK())
            .email("testuser@gmail.com")
            .build();
        userTable.putItem(userEntity);

        // When
        mockMvc.perform(delete("/users/me/jobs/unknown_job_id")
                .contentType("application/json"))
            .andExpect(status().isNoContent());

        // Then
        mockMvc.perform(get("/users/me/jobs")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

}
