package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDTO;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDTO;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Named("generateId")
    default String generateId(String prefix, String postfix, String id) {
        if (id == null) id = UUID.randomUUID().toString();
        if (prefix == null || id.startsWith(prefix)) prefix = "";
        else prefix = prefix + "#";
        if (postfix == null || id.endsWith(postfix)) postfix = "";
        else postfix = "#" + postfix;
        return prefix + id + postfix;
    }

    @Mapping(target = "pk", expression = "java(generateId(UserEntity.ID_NAME, null, userId))")
    @Mapping(target = "sk", expression = "java(generateId(UserEntity.ID_NAME, null, userId))")
    UserEntity toUserEntity(String userId, UserDto createUserDto);

    @Mapping(target = "pk", expression = "java(generateId(UserEntity.ID_NAME, UserJobEntity.ID_NAME, userId))")
    @Mapping(target = "sk", expression = "java(generateId(UserJobEntity.ID_NAME, null, job.jobId()))")
    @Mapping(target = "jobStartDate", source = "job.startDate")
    @Mapping(target = "jobEndDate", source = "job.endDate")
    @Mapping(target = "jobTitle", source = "job.jobTitle")
    @Mapping(target = "companyName", source = "job.companyName")
    @Mapping(target = "jobLocation", source = "job.location")
    @Mapping(target = "jobDescription", source = "job.description")
    UserJobEntity toJobEntity(String userId, UserJobDTO job);

    @Mapping(target = "pk", expression = "java(this.generateId(UserEntity.ID_NAME, UserEducationEntity.ID_NAME, userId))")
    @Mapping(target = "sk", expression = "java(this.generateId(UserEducationEntity.ID_NAME, null, education.educationId()))")
    UserEducationEntity toEducationEntity(String userId, UserEducationDTO education);

    default List<UserJobEntity> toJobEntities(String userId, List<UserJobDTO> jobs) {
        if (jobs == null) return List.of();
        return jobs.stream()
                .map(job -> toJobEntity(userId, job))
                .toList();
    }

    default List<UserEducationEntity> toEducationEntities(String userId, List<UserEducationDTO> education) {
        if (education == null) return List.of();
        return education.stream()
                .map(edu -> toEducationEntity(userId, edu))
                .toList();
    }

    @Mapping(target = "userId", expression = "java(userEntity.getPk().split(\"#\")[1])")
    @Mapping(target = "email", source = "userEntity.email")
    @Mapping(target = "firstName", source = "userEntity.firstName")
    @Mapping(target = "lastName", source = "userEntity.lastName")
    @Mapping(target = "birthday", source = "userEntity.birthday")
    @Mapping(target = "location", source = "userEntity.location")
    @Mapping(target = "jobs", source = "userJobEntities", qualifiedByName = "toUserJobDTOs")
    @Mapping(target = "education", source = "userEducationEntities", qualifiedByName = "toUserEducationDTOs")
    UserDto toUserDto(UserEntity userEntity, List<UserJobEntity> userJobEntities, List<UserEducationEntity> userEducationEntities);

    @Mapping(target = "jobId", expression = "java(userJobEntity.getSk().split(\"#\")[1])")
    @Mapping(target = "startDate", source = "userJobEntity.jobStartDate")
    @Mapping(target = "endDate", source = "userJobEntity.jobEndDate")
    @Mapping(target = "jobTitle", source = "userJobEntity.jobTitle")
    @Mapping(target = "companyName", source = "userJobEntity.companyName")
    @Mapping(target = "location", source = "userJobEntity.jobLocation")
    @Mapping(target = "description", source = "userJobEntity.jobDescription")
    UserJobDTO toUserJobDTO(UserJobEntity userJobEntity);

    @Mapping(target = "educationId", expression = "java(userEducationEntity.getSk().split(\"#\")[1])")
    UserEducationDTO toUserEducationDTO(UserEducationEntity userEducationEntity);

    @Named("toUserJobDTOs")
    default List<UserJobDTO> toUserJobDTOs(List<UserJobEntity> userJobEntities) {
        if (userJobEntities == null) return List.of();
        return userJobEntities.stream()
                .map(this::toUserJobDTO)
                .toList();
    }

    @Named("toUserEducationDTOs")
    default List<UserEducationDTO> toUserEducationDTOs(List<UserEducationEntity> userEducationEntities) {
        if (userEducationEntities == null) return List.of();
        return userEducationEntities.stream()
                .map(this::toUserEducationDTO)
                .toList();
    }
}