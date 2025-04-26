package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserJobMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserJobRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserJobService {

    private final UserJobRepository userJobRepository;
    private final UserJobMapper userJobMapper;

    public List<UserJobDto> getJobsForUser(String userId) {
        List<UserJobEntity> userJobEntities = userJobRepository.findAllUserJobs(userId);
        return userJobEntities.stream().map(userJobMapper::toUserJobDto)
                .toList();
    }

    public UserJobDto createUserJob(UserJobDto createUserJobDto, String name) {
        UserJobEntity userJobEntity = userJobMapper.toUserJobEntity(createUserJobDto, name);
        userJobRepository.saveUserJob(userJobEntity);
        return userJobMapper.toUserJobDto(userJobEntity);
    }

    public UserJobDto updateUserJob(String jobId, UserJobDto updateUserJobDto, String requestingUserId) {
        UserJobEntity userJobEntity = userJobMapper.toUserJobEntity(updateUserJobDto, requestingUserId);
        userJobEntity.setPk(UserEntity.generatePK(jobId));
        userJobRepository.updateUserJob(userJobEntity);
        return userJobMapper.toUserJobDto(userJobEntity);
    }

    public void deleteUserJob(String jobId, String requestingUserId) {
        userJobRepository.deleteUserJob(requestingUserId, jobId);
    }
}
