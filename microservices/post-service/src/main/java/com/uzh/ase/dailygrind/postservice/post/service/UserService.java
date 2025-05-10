package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.UserDto;
import com.uzh.ase.dailygrind.postservice.post.mapper.UserMapper;
import com.uzh.ase.dailygrind.postservice.post.repository.CommentRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.UserRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.FriendshipEvent;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.UserDataEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserDto getUser(String userId) {
        UserEntity userEntity = userRepository.getUser(userId);
        if (userEntity == null) {
            throw new IllegalArgumentException("User not found");
        }
        return userMapper.toUserDto(userEntity);
    }

    public void addNewUser(UserDataEvent userDataEvent) {
        UserEntity userEntity = userMapper.toUserEntity(userDataEvent);
        userRepository.addNewUser(userEntity);
    }

    public void updateUser(UserDataEvent userDataEvent) {
        UserEntity userEntity = userMapper.toUserEntity(userDataEvent);
        userRepository.updateUser(userEntity);
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId);
        postRepository.deleteAllPosts(userId);
        postRepository.deleteAllLikes(userId);
        commentRepository.deleteAllCommentsForUser(userId);
    }

    public void addFriend(FriendshipEvent friendshipEvent) {
        FriendEntity friendEntity = userMapper.toFriendEntity(friendshipEvent);
        userRepository.addFriend(friendEntity);
    }

    public void removeFriend(FriendshipEvent friendshipEvent) {
        FriendEntity friendEntity = userMapper.toFriendEntity(friendshipEvent);
        userRepository.removeFriend(friendEntity);
    }

    public List<UserDto> getFriends(String userId) {
        List<FriendEntity> friendEntities = userRepository.getFriends(userId);
        return friendEntities.stream()
            .map(friendEntity -> userMapper.toUserDto(userRepository.getUser(friendEntity.getFriendId())))
            .toList();
    }
}
