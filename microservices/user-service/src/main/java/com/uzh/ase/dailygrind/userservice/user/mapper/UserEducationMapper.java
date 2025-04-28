package com.uzh.ase.dailygrind.userservice.user.mapper;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEducationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEducationMapper {

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
