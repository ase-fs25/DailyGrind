package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.CreateUserDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDetailsDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target="userId", source = "userId")
    User createUserDtoToUserEntity(CreateUserDto createUserDto, String userId);


    UserDetailsDto userEntityToUserDetailsDto(User user);
}
