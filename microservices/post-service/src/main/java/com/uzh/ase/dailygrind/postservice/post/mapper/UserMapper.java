package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.UserDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.FriendshipEvent;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.UserDataEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link UserDto}, {@link UserEntity}, {@link FriendEntity},
 * {@link UserDataEvent}, and {@link FriendshipEvent}.
 * <p>
 * This interface provides methods to map data between user-related Data Transfer Objects (DTOs),
 * entities, and events related to user data and friendships. The mappings ensure proper conversion of
 * fields between these objects for interaction with the database and external event systems.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a {@link UserDataEvent} to a {@link UserEntity}.
     * <p>
     * This method converts a {@link UserDataEvent} into a {@link UserEntity}, which is used in the database
     * for storing user-related data such as email, first name, last name, and profile picture URL.
     * It also sets the primary key (PK) and sort key (SK) for the entity.
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
     * <p>
     * This method converts a {@link FriendshipEvent} into a {@link FriendEntity}, which is used to represent
     * the friendship relationship between two users in the database. It sets the primary and sort keys to identify
     * the users involved in the friendship.
     *
     * @param friendshipEvent the {@link FriendshipEvent} to map
     * @return the mapped {@link FriendEntity}
     */
    @Mapping(target = "pk", expression = "java(FriendEntity.generatePK(friendshipEvent.userAId()))")
    @Mapping(target = "sk", expression = "java(FriendEntity.generateSK(friendshipEvent.userBId()))")
    FriendEntity toFriendEntity(FriendshipEvent friendshipEvent);

    /**
     * Maps a {@link UserEntity} to a {@link UserDto}.
     * <p>
     * This method converts a {@link UserEntity} to a {@link UserDto}, which is used for transferring user-related
     * data in API responses. It extracts information such as user ID, email, first name, last name, and profile picture URL.
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
