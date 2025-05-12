package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserCreateDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.sns.events.UserDataEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between User-related DTOs, entities, and events.
 * <p>
 * This interface defines the mappings from {@link UserCreateDto} to {@link UserEntity},
 * from {@link UserEntity} to {@link UserInfoDto}, and from {@link UserEntity} to {@link UserDataEvent}.
 * MapStruct automatically generates the implementation for these conversions.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a {@link UserCreateDto} to a {@link UserEntity}.
     *
     * @param user the {@link UserCreateDto} to be mapped
     * @param userId the user ID to associate with the entity
     * @return the mapped {@link UserEntity}
     */
    @Mapping(target = "pk", expression = "java(UserEntity.generatePK(userId))")
    @Mapping(target = "sk", expression = "java(UserEntity.generateSK())")
    @Mapping(target = "numFriends", ignore = true)
    UserEntity toUserEntity(UserCreateDto user, String userId);

    /**
     * Converts a {@link UserEntity} to a {@link UserInfoDto}.
     *
     * @param user the {@link UserEntity} to be mapped
     * @param isFriend boolean indicating whether the requesting user is a friend or not
     * @return the mapped {@link UserInfoDto}
     */
    @Mapping(target = "userId", expression = "java(user.getId())")
    @Mapping(target = "numberOfFriends", source = "user.numFriends")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "birthday", source = "user.birthday")
    @Mapping(target = "location", source = "user.location")
    @Mapping(target = "profilePictureUrl", source = "user.profilePictureUrl")
    @Mapping(target = "isFriend", source = "isFriend")
    UserInfoDto toUserInfoDto(UserEntity user, boolean isFriend);

    /**
     * Converts a {@link UserEntity} to a {@link UserDataEvent}.
     *
     * @param userEntity the {@link UserEntity} to be mapped
     * @return the mapped {@link UserDataEvent}
     */
    @Mapping(target = "userId", expression = "java(userEntity.getId())")
    @Mapping(target = "email", source = "userEntity.email")
    @Mapping(target = "firstName", source = "userEntity.firstName")
    @Mapping(target = "lastName", source = "userEntity.lastName")
    @Mapping(target = "profilePictureUrl", source = "userEntity.profilePictureUrl")
    UserDataEvent toUserDataEvent(UserEntity userEntity);
}
