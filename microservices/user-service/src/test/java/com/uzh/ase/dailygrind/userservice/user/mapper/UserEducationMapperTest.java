package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

public class UserEducationMapperTest {

    UserEducationMapper userEducationMapper = Mappers.getMapper(UserEducationMapper.class);


    @Test
    void toUserEducationEntity_withId() {
        // given
        UserEducationDto userEducationDto = new UserEducationDto(
            UUID.randomUUID().toString(),
            "University of Zurich",
            "Bachelor of Science",
            "Computer Science",
            "2018-09-01",
            "2021-06-30",
            "Zurich",
            "Studied Computer Science fundamentals"
        );

        String userId = UUID.randomUUID().toString();

        // when
        UserEducationEntity userEducationEntity = userEducationMapper.toUserEducationEntity(userEducationDto, userId);

        // then
        assertThat(userEducationEntity)
            .isNotNull()
            .extracting(
                UserEducationEntity::getPk,
                UserEducationEntity::getSk,
                UserEducationEntity::getInstitution,
                UserEducationEntity::getEducationLocation,
                UserEducationEntity::getDegree,
                UserEducationEntity::getFieldOfStudy,
                UserEducationEntity::getEducationStartDate,
                UserEducationEntity::getEducationEndDate,
                UserEducationEntity::getEducationDescription
            )
            .containsExactly(
                "USER#"+userId+"#EDUCATION",
                "EDUCATION#"+userEducationDto.educationId(),
                "University of Zurich",
                "Zurich",
                "Bachelor of Science",
                "Computer Science",
                "2018-09-01",
                "2021-06-30",
                "Studied Computer Science fundamentals"
            );
    }

    @Test
    void toUserEducationEntity_withoutId() {
        // given
        UserEducationDto userEducationDto = new UserEducationDto(
            null,
            "University of Zurich",
            "Bachelor of Science",
            "Computer Science",
            "2018-09-01",
            "2021-06-30",
            "Zurich",
            "Studied Computer Science fundamentals"
        );

        String userId = UUID.randomUUID().toString();
        UUID mockedUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"); // <-- Create fixed UUID object

        try (MockedStatic<UUID> mockedStatic = mockStatic(UUID.class)) {
            // Proper mocking
            mockedStatic.when(UUID::randomUUID).thenReturn(mockedUUID);

            // when
            UserEducationEntity userEducationEntity = userEducationMapper.toUserEducationEntity(userEducationDto, userId);

            // then
            assertThat(userEducationEntity)
                .isNotNull()
                .extracting(
                    UserEducationEntity::getPk,
                    UserEducationEntity::getSk,
                    UserEducationEntity::getInstitution,
                    UserEducationEntity::getDegree,
                    UserEducationEntity::getFieldOfStudy,
                    UserEducationEntity::getEducationStartDate,
                    UserEducationEntity::getEducationEndDate,
                    UserEducationEntity::getEducationLocation,
                    UserEducationEntity::getEducationDescription
                )
                .containsExactly(
                    "USER#"+userId+"#EDUCATION",
                    "EDUCATION#"+mockedUUID,
                    "University of Zurich",
                    "Bachelor of Science",
                    "Computer Science",
                    "2018-09-01",
                    "2021-06-30",
                    "Zurich",
                    "Studied Computer Science fundamentals"
                );
        }
    }

    @Test
    void toUserEducationDto() {
        // given
        UserEducationEntity userEducationEntity = UserEducationEntity.builder()
            .pk("USER#12345#EDUCATION")
            .sk("EDUCATION#123e4567-e89b-12d3-a456-426614174000")
            .institution("University of Zurich")
            .degree("Bachelor of Science")
            .fieldOfStudy("Computer Science")
            .educationStartDate("2018-09-01")
            .educationEndDate("2021-06-30")
            .educationLocation("Zurich")
            .educationDescription("Studied Computer Science fundamentals")
            .build();

        // when
        UserEducationDto userEducationDto = userEducationMapper.toUserEducationDto(userEducationEntity);

        // then
        assertThat(userEducationDto)
            .isNotNull()
            .extracting(
                UserEducationDto::educationId,
                UserEducationDto::institution,
                UserEducationDto::degree,
                UserEducationDto::fieldOfStudy,
                UserEducationDto::startDate,
                UserEducationDto::endDate,
                UserEducationDto::location,
                UserEducationDto::description
            )
            .containsExactly(
                "123e4567-e89b-12d3-a456-426614174000",
                userEducationEntity.getInstitution(),
                userEducationEntity.getDegree(),
                userEducationEntity.getFieldOfStudy(),
                userEducationEntity.getEducationStartDate(),
                userEducationEntity.getEducationEndDate(),
                userEducationEntity.getEducationLocation(),
                userEducationEntity.getEducationDescription()
            );
    }


}
