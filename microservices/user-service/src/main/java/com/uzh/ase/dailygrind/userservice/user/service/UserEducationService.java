package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserEducationMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserEducationRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserEducationService {

    private final UserEducationRepository userEducationRepository;
    private final UserEducationMapper userEducationMapper;

    public List<UserEducationDto> getEducationForUser(String userId) {
        List<UserEducationEntity> userEducationDtos = userEducationRepository.findAllUserEducations(userId);
        return userEducationDtos.stream().map(userEducationMapper::toUserEducationDto)
                .toList();
    }

    public UserEducationDto createUserEducation(UserEducationDto createUserEducationDtos, String name) {
        UserEducationEntity userEducationEntity = userEducationMapper.toUserEducationEntity(createUserEducationDtos, name);
        userEducationRepository.saveUserEducation(userEducationEntity);
        return userEducationMapper.toUserEducationDto(userEducationEntity);
    }

    public UserEducationDto updateUserEducation(String educationId, UserEducationDto updateUserEducationDto, String name) {
        UserEducationEntity userEducationEntity = userEducationMapper.toUserEducationEntity(updateUserEducationDto, name);
        userEducationEntity.setSk(UserEducationEntity.generateSK(educationId));
        userEducationRepository.updateUserEducation(userEducationEntity);
        return userEducationMapper.toUserEducationDto(userEducationEntity);
    }

    public void deleteUserEducation(String name, String educationId) {
        userEducationRepository.deleteUserEducation(name, educationId);
    }
}
