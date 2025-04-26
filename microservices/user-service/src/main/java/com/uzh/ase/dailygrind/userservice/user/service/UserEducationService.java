package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEducationService {
    public List<UserEducationDto> getEducationForUser(String userId) {
        return null;
    }

    public List<UserEducationDto> createUserEducation(List<UserEducationDto> createUserEducationDtos, String name) {
        return null;
    }

    public UserEducationDto updateUserEducation(String educationId, UserEducationDto updateUserEducationDto, String name) {
        return null;
    }

    public void deleteUserEducation(String name, String educationId) {
    }
}
