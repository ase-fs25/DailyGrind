package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between {@link UserJobDto} and {@link UserJobEntity}.
 * <p>
 * This interface defines the conversion between DTO (Data Transfer Object) and Entity for User Job.
 * It uses MapStruct to automatically generate the implementation for the mapping logic.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserJobMapper {

    /**
     * Converts a {@link UserJobDto} to a {@link UserJobEntity}.
     *
     * @param userJobDto the user job DTO to be converted
     * @param userId the ID of the user associated with the job
     * @return the converted {@link UserJobEntity}
     */
    @Mapping(target = "pk", expression = "java(UserJobEntity.generatePK(userId))")
    @Mapping(target = "sk", expression = "java(UserJobEntity.generateSK(userJobDto.jobId()))")
    @Mapping(target = "jobTitle", source = "userJobDto.jobTitle")
    @Mapping(target = "companyName", source = "userJobDto.companyName")
    @Mapping(target = "jobStartDate", source = "userJobDto.startDate")
    @Mapping(target = "jobEndDate", source = "userJobDto.endDate")
    @Mapping(target = "jobLocation", source = "userJobDto.location")
    @Mapping(target = "jobDescription", source = "userJobDto.description")
    UserJobEntity toUserJobEntity(UserJobDto userJobDto, String userId);

    /**
     * Converts a {@link UserJobEntity} to a {@link UserJobDto}.
     *
     * @param userJobEntity the user job entity to be converted
     * @return the converted {@link UserJobDto}
     */
    @Mapping(target = "jobId", expression = "java(userJobEntity.getId())")
    @Mapping(target = "jobTitle", source = "userJobEntity.jobTitle")
    @Mapping(target = "companyName", source = "userJobEntity.companyName")
    @Mapping(target = "startDate", source = "userJobEntity.jobStartDate")
    @Mapping(target = "endDate", source = "userJobEntity.jobEndDate")
    @Mapping(target = "location", source = "userJobEntity.jobLocation")
    @Mapping(target = "description", source = "userJobEntity.jobDescription")
    UserJobDto toUserJobDto(UserJobEntity userJobEntity);
}
