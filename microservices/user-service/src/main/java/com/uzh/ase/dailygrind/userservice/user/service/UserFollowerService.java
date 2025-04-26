package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserInfoDto;
import com.uzh.ase.dailygrind.userservice.user.repository.UserFollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFollowerService {

    private final UserFollowerRepository userFollowerRepository;
    private final UserService userService;

    public Boolean isFollowing(String followingId, String followerId) {
        return userFollowerRepository.isFollowing(followingId, followerId);
    }

    public List<String> getFollowersIds(String userId) {
        return userFollowerRepository.findAllFollowers(userId);
    }

    public List<UserInfoDto> getFollowers(String userId) {
        List<String> followersIds = userFollowerRepository.findAllFollowers(userId);
        return followersIds.stream()
                .map(id -> userService.getUserInfo(id, userId))
                .toList();
    }

    public List<UserInfoDto> getFollowing(String userId) {
        List<String> followingIds = userFollowerRepository.findAllFollowing(userId);
        return followingIds.stream()
                .map(id -> userService.getUserInfo(id, userId))
                .toList();
    }

    public void followUser(String toFollowId, String userId) {
        userFollowerRepository.followUser(toFollowId, userId);
        userService.increaseFollowerCount(toFollowId);
        userService.increaseFollowingCount(userId);
    }

    public void unfollowUser(String toUnfollowId, String userId) {
        userFollowerRepository.unfollowUser(toUnfollowId, userId);
        userService.decreaseFollowerCount(toUnfollowId);
        userService.decreaseFollowingCount(userId);
    }
}
