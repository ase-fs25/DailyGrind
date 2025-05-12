package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between {@link UserEducationDto} and {@link UserEducationEntity}.
 * <p>
 * This interface defines the conversion between DTO (Data Transfer Object) and Entity for User Education.
 * It uses MapStruct to automatically generate the implementation for the mapping logic.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserEducationMapper {

    /**
     * Converts a {@link UserEducationDto} to a {@link UserEducationEntity}.
     *
     * @param userEducationDto the user education DTO to be converted
     * @param userId the ID of the user associated with the education
     * @return the converted {@link UserEducationEntity}
     */
    @Mapping(target = "pk", expression = "java(UserEducationEntity.generatePK(userId))")
    @Mapping(target = "sk", expression = "java(UserEducationEntity.generateSK(userEducationDto.educationId()))")
    @Mapping(target = "institution", source = "userEducationDto.institution")
    @Mapping(target = "educationLocation", source = "userEducationDto.location")
    @Mapping(target = "degree", source = "userEducationDto.degree")
    @Mapping(target = "fieldOfStudy", source = "userEducationDto.fieldOfStudy")
    @Mapping(target = "educationStartDate", source = "userEducationDto.startDate")
    @Mapping(target = "educationEndDate", source = "userEducationDto.endDate")
    @Mapping(target = "educationDescription", source = "userEducationDto.description")
    UserEducationEntity toUserEducationEntity(UserEducationDto userEducationDto, String userId);

    /**
     * Converts a {@link UserEducationEntity} to a {@link UserEducationDto}.
     *
     * @param userEducationEntity the user education entity to be converted
     * @return the converted {@link UserEducationDto}
     */
    @Mapping(target = "educationId", expression = "java(userEducationEntity.getId())")
    @Mapping(target = "institution", source = "userEducationEntity.institution")
    @Mapping(target = "degree", source = "userEducationEntity.degree")
    @Mapping(target = "fieldOfStudy", source = "userEducationEntity.fieldOfStudy")
    @Mapping(target = "startDate", source = "userEducationEntity.educationStartDate")
    @Mapping(target = "endDate", source = "userEducationEntity.educationEndDate")
    @Mapping(target = "location", source = "userEducationEntity.educationLocation")
    @Mapping(target = "description", source = "userEducationEntity.educationDescription")
    UserEducationDto toUserEducationDto(UserEducationEntity userEducationEntity);
}
