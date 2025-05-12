package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserEducationMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserEducationRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing user education data.
 * Provides methods to fetch, create, update, and delete education entries for a user.
 */
@Service
@RequiredArgsConstructor
public class UserEducationService {

    private final UserEducationRepository userEducationRepository;
    private final UserEducationMapper userEducationMapper;

    /**
     * Retrieves all education entries for a given user.
     *
     * @param userId the ID of the user
     * @return a list of {@link UserEducationDto} representing the user's education records
     */
    public List<UserEducationDto> getEducationForUser(String userId) {
        List<UserEducationEntity> userEducationDtos = userEducationRepository.findAllUserEducations(userId);
        return userEducationDtos.stream()
            .map(userEducationMapper::toUserEducationDto)
            .toList();
    }

    /**
     * Creates a new education entry for a user.
     *
     * @param createUserEducationDtos the DTO containing education information to be created
     * @param name the user's identifier (e.g. username or user ID)
     * @return the created {@link UserEducationDto}
     */
    public UserEducationDto createUserEducation(UserEducationDto createUserEducationDtos, String name) {
        UserEducationEntity userEducationEntity = userEducationMapper.toUserEducationEntity(createUserEducationDtos, name);
        userEducationRepository.saveUserEducation(userEducationEntity);
        return userEducationMapper.toUserEducationDto(userEducationEntity);
    }

    /**
     * Updates an existing education entry for a user.
     *
     * @param educationId the ID of the education entry to update
     * @param updateUserEducationDto the DTO containing updated education information
     * @param name the user's identifier
     * @return the updated {@link UserEducationDto}
     */
    public UserEducationDto updateUserEducation(String educationId, UserEducationDto updateUserEducationDto, String name) {
        UserEducationEntity userEducationEntity = userEducationMapper.toUserEducationEntity(updateUserEducationDto, name);
        userEducationEntity.setSk(UserEducationEntity.generateSK(educationId));
        userEducationRepository.updateUserEducation(userEducationEntity);
        return userEducationMapper.toUserEducationDto(userEducationEntity);
    }

    /**
     * Deletes a specific education entry for a user.
     *
     * @param name the user's identifier
     * @param educationId the ID of the education entry to delete
     */
    public void deleteUserEducation(String name, String educationId) {
        userEducationRepository.deleteUserEducation(name, educationId);
    }

    /**
     * Deletes all education entries associated with a given user.
     *
     * @param userId the ID of the user
     */
    public void deleteEducationForUser(String userId) {
        List<UserEducationEntity> userEducationEntities = userEducationRepository.findAllUserEducations(userId);
        for (UserEducationEntity userEducationEntity : userEducationEntities) {
            userEducationRepository.deleteUserEducation(userId, userEducationEntity.getSk());
        }
    }
}
