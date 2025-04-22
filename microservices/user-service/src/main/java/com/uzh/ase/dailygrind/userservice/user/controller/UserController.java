package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDto;
import com.uzh.ase.dailygrind.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUserDetailsById(@PathVariable String userId) {
        return userService.getUserDetailsById(userId);
    }

    @GetMapping("/me")
    public UserDto getMyDetails(Principal principal) {
        return userService.getUserDetailsById(principal.getName());
    }

    @GetMapping
    public List<UserDto> getUsersDetails() {
        return userService.getAllUserDetails();
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto createUserDto, Principal principal) {
        UserDto createdUser = userService.createUser(createUserDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("follow/{userId}")
    public ResponseEntity<?> followUser(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) {
            return ResponseEntity
                    .badRequest()
                    .body("You cannot follow yourself.");
        }
        userService.followUser(userId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("unfollow/{userId}")
    public ResponseEntity<?> unfollowUser(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) {
            return ResponseEntity
                    .badRequest()
                    .body("You cannot unfollow yourself.");
        }
        userService.unfollowUser(userId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/followers")
    public List<UserDto> getFollowers(@PathVariable String userId) {
        return userService.getFollowers(userId);
    }

    @GetMapping("/{userId}/following")
    public List<UserDto> getFollowing(@PathVariable String userId) {
        return userService.getFollowing(userId);
    }

    @GetMapping("/followers")
    public List<UserDto> getMyFollowers(Principal principal) {
        return userService.getFollowers(principal.getName());
    }

    @GetMapping("/following")
    public List<UserDto> getMyFollowing(Principal principal) {
        return userService.getFollowing(principal.getName());
    }
}