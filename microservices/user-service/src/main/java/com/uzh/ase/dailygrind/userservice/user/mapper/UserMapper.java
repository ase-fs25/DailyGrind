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

    @Named("generatePk")
    public static String generatePk(String userId) {
        return "USER#" + userId;
    }

    @Named("generateJobSk")
    public static String generateJobSk(String sk) {
        return (sk == null || sk.isEmpty()) ? "JOB#" + UUID.randomUUID() : sk;
    }

    @Named("generateEducationSk")
    public static String generateEducationSk(String sk) {
        return (sk == null || sk.isEmpty()) ? "EDUCATION#" + UUID.randomUUID() : sk;
    }

    @Mapping(target = "pk", source = "userId", qualifiedByName = "generatePk")
    @Mapping(target = "sk", source = "userId", qualifiedByName = "generatePk")
    UserEntity toUserEntity(String userId, UserDto createUserDto);

    @Mapping(target = "pk", source = "userId", qualifiedByName = "generatePk")
    @Mapping(target = "sk", source = "job.jobId", qualifiedByName = "generateJobSk")
    UserJobEntity toJobEntity(String userId, UserJobDTO job);

    @Mapping(target = "pk", source = "userId", qualifiedByName = "generatePk")
    @Mapping(target = "sk", source = "education.educationId", qualifiedByName = "generateEducationSk")
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

    @Mapping(target = "userId", expression = "java(userEntity.getPk().replace(\"USER#\", \"\"))")
    @Mapping(target = "email", source = "userEntity.email")
    @Mapping(target = "firstName", source = "userEntity.firstName")
    @Mapping(target = "lastName", source = "userEntity.lastName")
    @Mapping(target = "birthday", source = "userEntity.birthday")
    @Mapping(target = "location", source = "userEntity.location")
    @Mapping(target = "jobs", source = "userJobEntities", qualifiedByName = "toUserJobDTOs")
    @Mapping(target = "education", source = "userEducationEntities", qualifiedByName = "toUserEducationDTOs")
    UserDto toUserDto(UserEntity userEntity, List<UserJobEntity> userJobEntities, List<UserEducationEntity> userEducationEntities);

    @Mapping(target = "jobId", expression = "java(userJobEntity.getSk().replace(\"JOB#\", \"\"))")
    UserJobDTO toUserJobDTO(UserJobEntity userJobEntity);

    @Mapping(target = "educationId", expression = "java(userEducationEntity.getSk().replace(\"EDUCATION#\", \"\"))")
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