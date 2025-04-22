package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.UserEntity;
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

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAllUser();
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto createUserDto, Principal principal) {
        UserDto createdUser = userService.createUser(createUserDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

//    @GetMapping("/page")
//    public List<UserEntity> getUsersPage(int page, int size) {
//        return userService.getUsersPage(PageRequest.of(page, size)).getContent();
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<UserDetailsDto> getUserDetailsById(@PathVariable("id") String id) {
//        UserDetailsDto userDetailsDto = userService.getUserById(id);
//        if (userDetailsDto != null) {
//            return ResponseEntity.ok(userDetailsDto);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }
}