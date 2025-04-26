package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

public class UserJobMapperTest {

    UserJobMapper userJobMapper = Mappers.getMapper(UserJobMapper.class);

    @Test
    void toUserJobEntity_withId() {
        // given
        UserJobDto userJobDto = new UserJobDto(
            UUID.randomUUID().toString(),
            "Software Engineer",
            "Google",
            "2022-01-01",
            "2023-01-01",
            "New York",
            "Developed software applications"
        );

        String userId = UUID.randomUUID().toString();

        // when
        UserJobEntity userJobEntity = userJobMapper.toUserJobEntity(userJobDto, userId);

        // then
        assertThat(userJobEntity)
            .isNotNull()
            .extracting(UserJobEntity::getPk, UserJobEntity::getSk, UserJobEntity::getJobTitle, UserJobEntity::getCompanyName, UserJobEntity::getJobStartDate, UserJobEntity::getJobEndDate, UserJobEntity::getJobLocation, UserJobEntity::getJobDescription
            )
            .containsExactly(
                "USER#"+userId+"#JOB", "JOB#"+userJobDto.jobId(), userJobDto.jobTitle(), userJobDto.companyName(), userJobDto.startDate(), userJobDto.endDate(), userJobDto.location(), userJobDto.description()
            );
    }

    @Test
    void toUserJobEntity_withoutId() {
        // given
        UserJobDto userJobDto = new UserJobDto(
            null,
            "Software Engineer",
            "Google",
            "2022-01-01",
            "2023-01-01",
            "New York",
            "Developed software applications"
        );

        String userId = UUID.randomUUID().toString();
        UUID mockedUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"); // <-- Create fixed UUID object

        try (MockedStatic<UUID> mockedStatic = mockStatic(UUID.class)) {
            // Proper mocking
            mockedStatic.when(UUID::randomUUID).thenReturn(mockedUUID);

            // when
            UserJobEntity userJobEntity = userJobMapper.toUserJobEntity(userJobDto, userId);

            // then
            assertThat(userJobEntity)
                .isNotNull()
                .extracting(
                    UserJobEntity::getPk,
                    UserJobEntity::getSk,
                    UserJobEntity::getJobTitle,
                    UserJobEntity::getCompanyName,
                    UserJobEntity::getJobStartDate,
                    UserJobEntity::getJobEndDate,
                    UserJobEntity::getJobLocation,
                    UserJobEntity::getJobDescription
                )
                .containsExactly(
                    "USER#" + userId + "#JOB",
                    "JOB#" + mockedUUID,
                    userJobDto.jobTitle(),
                    userJobDto.companyName(),
                    userJobDto.startDate(),
                    userJobDto.endDate(),
                    userJobDto.location(),
                    userJobDto.description()
                );
        }
    }

    @Test
    void toUserJobDto() {
        // given
        UserJobEntity userJobEntity = UserJobEntity.builder()
            .pk("USER#12345#JOB")
            .sk("JOB#123e4567-e89b-12d3-a456-426614174000")
            .jobTitle("Software Engineer")
            .companyName("Google")
            .jobStartDate("2022-01-01")
            .jobEndDate("2023-01-01")
            .jobLocation("New York")
            .jobDescription("Developed software applications")
            .build();

        // when
        UserJobDto userJobDto = userJobMapper.toUserJobDto(userJobEntity);

        // then
        assertThat(userJobDto)
            .isNotNull()
            .extracting(UserJobDto::jobId, UserJobDto::jobTitle, UserJobDto::companyName, UserJobDto::startDate, UserJobDto::endDate, UserJobDto::location, UserJobDto::description)
            .containsExactly(
                "123e4567-e89b-12d3-a456-426614174000",
                userJobEntity.getJobTitle(),
                userJobEntity.getCompanyName(),
                userJobEntity.getJobStartDate(),
                userJobEntity.getJobEndDate(),
                userJobEntity.getJobLocation(),
                userJobEntity.getJobDescription()
            );
    }
}
