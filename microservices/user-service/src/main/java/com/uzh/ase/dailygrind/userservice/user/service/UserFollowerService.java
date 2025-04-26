package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserCreateDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFollowerService {
    public Boolean isFollowing(String name, String userId) {
        return null;
    }

    public List<UserCreateDto> getFollowers(String name) {
        return null;
    }

    public String[] getFollowersIds(String name) {
        return null;
    }

    public List<UserCreateDto> getFollowing(String name) {
        return null;
    }

    public void followUser(String userId, String name) {
    }

    public void unfollowUser(String userId, String name) {
    }
}
