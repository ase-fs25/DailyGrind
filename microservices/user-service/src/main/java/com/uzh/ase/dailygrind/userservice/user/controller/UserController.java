package com.uzh.ase.dailygrind.userservice.user.controller;

import com.uzh.ase.dailygrind.userservice.user.controller.dto.CreateUserDto;
import com.uzh.ase.dailygrind.userservice.user.controller.dto.UserDetailsDto;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.User;
import com.uzh.ase.dailygrind.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<User> getUsers() {
        return userService.getAllUser();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserDto createUserDto, Principal principal) {
        User createdUser = userService.createUser(createUserDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/page")
    public Page<User> getUsersPage(Pageable pageable) {
        return userService.getUsersPage(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> getUserDetailsById(@PathVariable("id") String id) {
        UserDetailsDto userDetailsDto = userService.getUserById(id);
        if (userDetailsDto != null) {
            return ResponseEntity.ok(userDetailsDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}