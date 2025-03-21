package com.uzh.ase.dailygrind.userservice.user.service;

import com.uzh.ase.dailygrind.userservice.user.repository.UserCrudRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.UserPagingSortingRepository;
import com.uzh.ase.dailygrind.userservice.user.repository.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCrudRepository userCrudRepository;

    private final UserPagingSortingRepository userPagingSortingRepository;

    public List<User> getAllUser() {
        return userCrudRepository.findAll();
    }

    public User createUser(User user) {
        return userCrudRepository.save(user);
    }

    public Page<User> getUsersPage(Pageable pageable) {
        return userPagingSortingRepository.findAll(pageable);
    }

    public User getUserById(String id) {
        return userPagingSortingRepository.findById(id);
    }

}
