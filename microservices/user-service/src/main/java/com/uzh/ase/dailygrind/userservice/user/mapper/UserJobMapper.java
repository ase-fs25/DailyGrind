package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserJobMapper {

    @Mapping(target = "pk", expression = "java(UserJobEntity.generatePK(userId))")
    @Mapping(target = "sk", expression = "java(UserJobEntity.generateSK(userJobDto.jobId()))")
    @Mapping(target = "jobTitle", source = "userJobDto.jobTitle")
    @Mapping(target = "companyName", source = "userJobDto.companyName")
    @Mapping(target = "jobStartDate", source = "userJobDto.startDate")
    @Mapping(target = "jobEndDate", source = "userJobDto.endDate")
    @Mapping(target = "jobLocation", source = "userJobDto.location")
    @Mapping(target = "jobDescription", source = "userJobDto.description")
    UserJobEntity toUserJobEntity(UserJobDto userJobDto, String userId);

    @Mapping(target = "jobId", expression = "java(userJobEntity.getId())")
    @Mapping(target = "jobTitle", source = "userJobEntity.jobTitle")
    @Mapping(target = "companyName", source = "userJobEntity.companyName")
    @Mapping(target = "startDate", source = "userJobEntity.jobStartDate")
    @Mapping(target = "endDate", source = "userJobEntity.jobEndDate")
    @Mapping(target = "location", source = "userJobEntity.jobLocation")
    @Mapping(target = "description", source = "userJobEntity.jobDescription")
    UserJobDto toUserJobDto(UserJobEntity userJobEntity);
}
