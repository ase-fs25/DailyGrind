package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserCreateDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "pk", expression = "java(UserEntity.generatePK(userId))")
    @Mapping(target = "sk", expression = "java(UserEntity.generateSK())")
    UserEntity toUserEntity(UserCreateDto user, String userId);

    @Mapping(target = "userId", expression = "java(user.getId())")
    @Mapping(target = "numberOfFriends", source = "user.numFriends")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "birthday", source = "user.birthday")
    @Mapping(target = "location", source = "user.location")
    @Mapping(target = "profilePictureUrl", source = "user.profilePictureUrl")
    @Mapping(target = "isFriend", source = "isFriend")
    @Mapping(target = "requestId", ignore = true)
    UserInfoDto toUserInfoDto(UserEntity user, boolean isFriend);
}
