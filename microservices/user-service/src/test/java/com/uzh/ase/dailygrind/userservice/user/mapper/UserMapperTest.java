package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserCreateDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserEntity_shouldMapFieldsCorrectly() {
        // given
        String userId = "12345";
        UserCreateDto userCreateDto = new UserCreateDto(
                userId,
                "test@example.com",
                "John",
                "Doe",
                "1990-01-01",
                "New York",
                "http://example.com/profile.jpg"
        );

        // when
        UserEntity userEntity = userMapper.toUserEntity(userCreateDto, userId);

        // then
        assertThat(userEntity)
                .isNotNull()
                .extracting(
                        UserEntity::getPk,
                        UserEntity::getSk,
                        UserEntity::getEmail,
                        UserEntity::getFirstName,
                        UserEntity::getLastName,
                        UserEntity::getBirthday,
                        UserEntity::getLocation,
                        UserEntity::getProfilePictureUrl,
                        UserEntity::getNumFriends
                )
                .containsExactly(
                        "USER#12345",
                        "INFO",
                        "test@example.com",
                        "John",
                        "Doe",
                        "1990-01-01",
                        "New York",
                        userCreateDto.profilePictureUrl(),
                        0
                );
    }

    @Test
    void toUserInfoDto_shouldMapFieldsCorrectly() {
        // given
        UserEntity userEntity = UserEntity.builder()
                .pk("USER#12345")
                .sk("INFO")
                .firstName("John")
                .email("john.doe@gmail.com")
                .lastName("Doe")
                .profilePictureUrl("profile.jpg")
                .birthday("1990-01-01")
                .location("New York")
                .numFriends(10)
                .build();
        boolean isFriend = true;

        // when
        UserInfoDto dto = userMapper.toUserInfoDto(userEntity, isFriend);

        // then
        assertThat(dto)
                .isNotNull()
                .extracting(
                        UserInfoDto::userId,
                        UserInfoDto::email,
                        UserInfoDto::firstName,
                        UserInfoDto::lastName,
                        UserInfoDto::birthday,
                        UserInfoDto::location,
                        UserInfoDto::profilePictureUrl,
                        UserInfoDto::numberOfFriends,
                        UserInfoDto::isFriend
                )
                .containsExactly(
                        "12345",
                        userEntity.getEmail(),
                        userEntity.getFirstName(),
                        userEntity.getLastName(),
                        userEntity.getBirthday(),
                        userEntity.getLocation(),
                        userEntity.getProfilePictureUrl(),
                        userEntity.getNumFriends(),
                        isFriend
                );
    }
}
