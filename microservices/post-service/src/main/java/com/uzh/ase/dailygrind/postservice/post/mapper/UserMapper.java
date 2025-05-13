package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.UserDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.FriendshipEvent;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.UserDataEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between {@link UserDto}, {@link UserEntity}, {@link FriendEntity},
 * {@link UserDataEvent}, and {@link FriendshipEvent}.
 * <p>
 * This interface uses MapStruct to automatically generate the implementation for converting between
 * user data events, friendship events, and the corresponding entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a {@link UserDataEvent} to a {@link UserEntity}.
     *
     * @param userDataEvent the {@link UserDataEvent} to map
     * @return the mapped {@link UserEntity}
     */
    @Mapping(target = "pk", expression = "java(UserEntity.generatePK(userDataEvent.userId()))")
    @Mapping(target = "sk", expression = "java(UserEntity.generateSK())")
    @Mapping(target = "email", source = "userDataEvent.email")
    @Mapping(target = "firstName", source = "userDataEvent.firstName")
    @Mapping(target = "lastName", source = "userDataEvent.lastName")
    @Mapping(target = "profilePictureUrl", source = "userDataEvent.profilePictureUrl")
    UserEntity toUserEntity(UserDataEvent userDataEvent);

    /**
     * Maps a {@link FriendshipEvent} to a {@link FriendEntity}.
     *
     * @param friendshipEvent the {@link FriendshipEvent} to map
     * @return the mapped {@link FriendEntity}
     */
    @Mapping(target = "pk", expression = "java(FriendEntity.generatePK(friendshipEvent.userAId()))")
    @Mapping(target = "sk", expression = "java(FriendEntity.generateSK(friendshipEvent.userBId()))")
    FriendEntity toFriendEntity(FriendshipEvent friendshipEvent);

    /**
     * Maps a {@link UserEntity} to a {@link UserDto}.
     *
     * @param userEntity the {@link UserEntity} to map
     * @return the mapped {@link UserDto}
     */
    @Mapping(target = "userId", expression = "java(userEntity.getUserId())")
    @Mapping(target = "email", source = "userEntity.email")
    @Mapping(target = "firstName", source = "userEntity.firstName")
    @Mapping(target = "lastName", source = "userEntity.lastName")
    @Mapping(target = "profilePictureUrl", source = "userEntity.profilePictureUrl")
    UserDto toUserDto(UserEntity userEntity);
}
