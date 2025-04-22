package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDto;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public List<UserDto> getAllUser() {
        return userRepository.findAllUserDetails();
    }

    public UserDto createUser(UserDto createUserDto, String userId) {
        UserEntity userEntity = userMapper.toUserEntity(userId, createUserDto);
        List<UserJobEntity> userJobEntities = userMapper.toJobEntities(userId, createUserDto.jobs());
        List<UserEducationEntity> userEducationEntities = userMapper.toEducationEntities(userId, createUserDto.education());

        userRepository.save(userEntity, userJobEntities, userEducationEntities);

        return userMapper.toUserDto(userEntity, userJobEntities, userEducationEntities);
    }

//    public Page<UserEntity> getUsersPage(Pageable pageable) {
//        return userPagingSortingRepository.findAll(pageable);
//    }
//
//    public UserDetailsDto getUserById(String id) {
//        UserEntity user = userPagingSortingRepository.findById(id);
//        return userMapper.userEntityToUserDetailsDto(user);
//    }

}
