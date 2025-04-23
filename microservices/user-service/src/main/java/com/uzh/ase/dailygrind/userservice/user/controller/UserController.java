package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserEducationDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserJobDto;
import com.uzh.ase.dailygrind.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public UserDto getUserDetailsById(@PathVariable String userId) {
        return userService.getUserDetailsById(userId);
    }

    @GetMapping("/me")
    public UserDto getMyDetails(Principal principal) {
        return userService.getUserDetailsById(principal.getName());
    }

    @GetMapping("/users")
    public List<UserDto> getUsersDetails() {
        return userService.getAllUserDetails();
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto createUserDto, Principal principal) {
        UserDto createdUser = userService.createUser(createUserDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/me/followers/{userId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) return ResponseEntity.ok(false);
        boolean isFollowing = userService.isFollowing(userId, principal.getName());
        return ResponseEntity.ok(isFollowing);
    }

    @GetMapping("/me/following/{userId}")
    public ResponseEntity<Boolean> isFollowed(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) return ResponseEntity.ok(false);
        boolean isFollowed = userService.isFollowing(principal.getName(), userId);
        return ResponseEntity.ok(isFollowed);
    }

    @PutMapping("/me/following/{userId}")
    public ResponseEntity<?> followUser(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) {
            return ResponseEntity
                    .badRequest()
                    .body("You cannot follow yourself.");
        }
        userService.followUser(userId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/following/{userId}")
    public ResponseEntity<?> unfollowUser(@PathVariable String userId, Principal principal) {
        if (principal.getName().equals(userId)) {
            return ResponseEntity
                    .badRequest()
                    .body("You cannot unfollow yourself.");
        }
        userService.unfollowUser(userId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}/followers")
    public List<UserDto> getFollowers(@PathVariable String userId) {
        return userService.getFollowers(userId);
    }

    @GetMapping("/users/{userId}/following")
    public List<UserDto> getFollowing(@PathVariable String userId) {
        return userService.getFollowing(userId);
    }

    @GetMapping("/me/followers")
    public List<UserDto> getMyFollowers(Principal principal) {
        return userService.getFollowers(principal.getName());
    }

    @GetMapping("/me/following")
    public List<UserDto> getMyFollowing(Principal principal) {
        return userService.getFollowing(principal.getName());
    }

    @GetMapping("/users/{userId}/jobs")
    public List<UserJobDto> getUserJobs(@PathVariable String userId) {
        return userService.getJobsForUser(userId);
    }

    @GetMapping("/users/{userId}/education")
    public List<UserEducationDto> getUserEducation(@PathVariable String userId) {
        return userService.getEducationForUser(userId);
    }

    @GetMapping("/me/jobs")
    public List<UserJobDto> getMyJobs(Principal principal) {
        return userService.getJobsForUser(principal.getName());
    }

    @GetMapping("/me/education")
    public List<UserEducationDto> getMyEducations(Principal principal) {
        return userService.getEducationForUser(principal.getName());
    }

    @DeleteMapping("/me/jobs/{jobId}")
    public ResponseEntity<?> deleteUserJob(@PathVariable String jobId, Principal principal) {
        userService.deleteUserJob(principal.getName(), jobId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/education/{educationId}")
    public ResponseEntity<?> deleteUserEducation(@PathVariable String educationId, Principal principal) {
        userService.deleteUserEducation(principal.getName(), educationId);
        return ResponseEntity.ok().build();
    }
}