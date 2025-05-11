package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.UserDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.FriendshipEvent;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.UserDataEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserEntity_shouldMapCorrectly() {
        UserDataEvent event = new UserDataEvent("user123", "test@example.com", "Alice", "Smith", "http://pic.com/me.png");

        UserEntity entity = userMapper.toUserEntity(event);

        assertThat(entity.getPk()).isEqualTo("USER#user123#INFO");
        assertThat(entity.getSk()).isEqualTo("INFO");
        assertThat(entity.getEmail()).isEqualTo("test@example.com");
        assertThat(entity.getFirstName()).isEqualTo("Alice");
        assertThat(entity.getLastName()).isEqualTo("Smith");
        assertThat(entity.getProfilePictureUrl()).isEqualTo("http://pic.com/me.png");
    }

    @Test
    void toFriendEntity_shouldMapCorrectly() {
        FriendshipEvent event = new FriendshipEvent("user123", "user456");

        FriendEntity friendEntity = userMapper.toFriendEntity(event);

        assertThat(friendEntity.getPk()).isEqualTo("USER#user123#FRIEND");
        assertThat(friendEntity.getSk()).isEqualTo("FRIEND#user456");
    }

    @Test
    void toUserDto_shouldMapCorrectly() {
        UserEntity entity = new UserEntity();
        entity.setPk("USER#user123");
        entity.setSk("PROFILE");
        entity.setEmail("example@test.com");
        entity.setFirstName("Bob");
        entity.setLastName("Jones");
        entity.setProfilePictureUrl("http://pic.com/avatar.png");

        UserDto dto = userMapper.toUserDto(entity);

        assertThat(dto.userId()).isEqualTo("user123");
        assertThat(dto.email()).isEqualTo("example@test.com");
        assertThat(dto.firstName()).isEqualTo("Bob");
        assertThat(dto.lastName()).isEqualTo("Jones");
        assertThat(dto.profilePictureUrl()).isEqualTo("http://pic.com/avatar.png");
    }
}
