package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserCreateDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a UserCreateDto to a UserEntity.
     *
     * @param user the UserCreateDto to map
     * @return the mapped UserEntity
     */
    @Mapping(target = "pk", expression = "java(UserEntity.generatePK(userId))")
    @Mapping(target = "sk", expression = "java(UserEntity.generateSK())")
    UserEntity toUserEntity(UserCreateDto user, String userId);

    /**
     * Maps a UserEntity to a UserInfoDto.
     *
     * @param user the UserEntity to map
     * @param isFollowing a boolean that indicates whether the requesting user follows or not
     * @return the mapped UserInfoDto
     */
    @Mapping(target = "userId", expression = "java(user.getId())")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "birthday", source = "user.birthday")
    @Mapping(target = "location", source = "user.location")
    @Mapping(target = "numFollowers", source = "user.numFollowers")
    @Mapping(target = "numFollowing", source = "user.numFollowing")
    @Mapping(target = "isFollowed", source = "isFollowed")
    @Mapping(target = "profilePictureUrl", source = "user.profilePictureUrl")
    UserInfoDto toUserInfoDto(UserEntity user, boolean isFollowed);



}
