package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.UserDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.FriendshipEvent;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.UserDataEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a UserDataEvent to a UserEntity.
     *
     * @param userDataEvent the UserDataEvent to map
     * @return the mapped UserEntity
     */
    @Mapping(target = "pk", expression = "java(UserEntity.generatePK(userDataEvent.userId()))")
    @Mapping(target = "sk", expression = "java(UserEntity.generateSK(userDataEvent.userId()))")
    @Mapping(target = "email", source = "userDataEvent.email")
    @Mapping(target = "firstName", source = "userDataEvent.firstName")
    @Mapping(target = "lastName", source = "userDataEvent.lastName")
    @Mapping(target = "profilePictureUrl", source = "userDataEvent.profilePictureUrl")
    UserEntity toUserEntity(UserDataEvent userDataEvent);

    /**
     * Maps a FriendshipEvent to a FriendEntity.
     *
     * @param friendshipEvent the FriendshipEvent to map
     * @return the mapped FriendEntity
     */
    @Mapping(target = "pk", expression = "java(FriendEntity.generatePK(friendshipEvent.userAId()))")
    @Mapping(target = "sk", expression = "java(FriendEntity.generateSK(friendshipEvent.userBId()))")
    FriendEntity toFriendEntity(FriendshipEvent friendshipEvent);

    /**
     * Maps a UserEntity to a UserDto.
     *
     * @param userEntity the UserEntity to map
     * @return the mapped UserDto
     */
    @Mapping(target = "userId", expression = "java(userEntity.getUserId())")
    @Mapping(target = "email", source = "userEntity.email")
    @Mapping(target = "firstName", source = "userEntity.firstName")
    @Mapping(target = "lastName", source = "userEntity.lastName")
    @Mapping(target = "profilePictureUrl", source = "userEntity.profilePictureUrl")
    UserDto toUserDto(UserEntity userEntity);
}
