package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserCreateDto;
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
                "New York"
        );

        // when
        UserEntity userEntity = userMapper.toUserEntity(userCreateDto, userId);

        // then
        assertThat(userEntity)
                .isNotNull()
                .extracting(UserEntity::getPk, UserEntity::getSk, UserEntity::getEmail, UserEntity::getFirstName, UserEntity::getLastName, UserEntity::getBirthday, UserEntity::getLocation, UserEntity::getProfilePicture, UserEntity::getNumFollowers, UserEntity::getNumFollowing
                )
                .containsExactly("USER#12345", "INFO", "test@example.com", "John", "Doe", "1990-01-01", "New York", null, 0, 0);
    }

}
