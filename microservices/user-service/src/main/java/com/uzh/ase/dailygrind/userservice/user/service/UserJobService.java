package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
import com.uzh.ase.dailygrind.userservice.user.mapper.UserJobMapper;
import com.uzh.ase.dailygrind.userservice.user.repository.UserJobRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing user job entries.
 * Supports creation, retrieval, update, and deletion of job information associated with a user.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserJobService {

    private final UserJobRepository userJobRepository;
    private final UserJobMapper userJobMapper;

    /**
     * Retrieves all job entries for a given user.
     *
     * @param userId ID of the user whose jobs are being retrieved
     * @return list of {@link UserJobDto} representing the user's job entries
     */
    public List<UserJobDto> getJobsForUser(String userId) {
        log.info("Fetching job entries for user: {}", userId);
        List<UserJobEntity> userJobEntities = userJobRepository.findAllUserJobs(userId);
        log.info("Retrieved {} job entries for user: {}", userJobEntities.size(), userId);
        return userJobEntities.stream().map(userJobMapper::toUserJobDto).toList();
    }

    /**
     * Creates a new job entry for a user.
     *
     * @param createUserJobDto job data to be created
     * @param name             ID of the user creating the job
     * @return the created {@link UserJobDto}
     */
    public UserJobDto createUserJob(UserJobDto createUserJobDto, String name) {
        log.info("Creating new job for user: {}", name);
        UserJobEntity userJobEntity = userJobMapper.toUserJobEntity(createUserJobDto, name);
        userJobRepository.saveUserJob(userJobEntity);
        log.info("Successfully created job with ID: {}", userJobEntity.getSk());
        return userJobMapper.toUserJobDto(userJobEntity);
    }

    /**
     * Updates an existing job entry for a user.
     *
     * @param jobId              ID of the job to be updated
     * @param updateUserJobDto   updated job data
     * @param requestingUserId   ID of the user requesting the update
     * @return the updated {@link UserJobDto}
     */
    public UserJobDto updateUserJob(String jobId, UserJobDto updateUserJobDto, String requestingUserId) {
        log.info("Updating job with ID: {}", jobId);
        UserJobEntity userJobEntity = userJobMapper.toUserJobEntity(updateUserJobDto, requestingUserId);
        userJobEntity.setSk(UserJobEntity.generateSK(jobId));
        userJobRepository.updateUserJob(userJobEntity);
        log.info("Successfully updated job with ID: {}", jobId);
        return userJobMapper.toUserJobDto(userJobEntity);
    }

    /**
     * Deletes a specific job entry for a user.
     *
     * @param jobId            ID of the job to be deleted
     * @param requestingUserId ID of the user requesting the deletion
     */
    public void deleteUserJob(String jobId, String requestingUserId) {
        log.info("Deleting job with ID: {} for user: {}", jobId, requestingUserId);
        userJobRepository.deleteUserJob(requestingUserId, jobId);
        log.info("Successfully deleted job with ID: {} for user: {}", jobId, requestingUserId);
    }

    /**
     * Deletes all job entries associated with a specific user.
     *
     * @param userId ID of the user whose job entries should be deleted
     */
    public void deleteJobsForUser(String userId) {
        log.info("Deleting all jobs for user: {}", userId);
        List<UserJobEntity> userJobEntities = userJobRepository.findAllUserJobs(userId);
        for (UserJobEntity userJobEntity : userJobEntities) {
            userJobRepository.deleteUserJob(userId, userJobEntity.getSk());
            log.info("Deleted job with ID: {} for user: {}", userJobEntity.getSk(), userId);
        }
    }
}
