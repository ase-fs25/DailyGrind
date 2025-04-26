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
                    "USER#" + userId + "#JOB", // notice .toString() here
                    "JOB#" + mockedUUID.toString(), // notice .toString() here
                    userJobDto.jobTitle(),
                    userJobDto.companyName(),
                    userJobDto.startDate(),
                    userJobDto.endDate(),
                    userJobDto.location(),
                    userJobDto.description()
                );
        }
    }
}
