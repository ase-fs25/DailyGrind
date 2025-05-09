package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.mapper.UserMapper;
import com.uzh.ase.dailygrind.postservice.post.repository.PostRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.UserRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.FriendEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.UserEntity;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.FriendshipEvent;
import com.uzh.ase.dailygrind.postservice.post.sqs.events.UserDataEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

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
        postRepository.deleteAllComments(userId);
        postRepository.deleteAllLikes(userId);
    }

    public void addFriend(FriendshipEvent friendshipEvent) {
        FriendEntity friendEntity = userMapper.toFriendEntity(friendshipEvent);
        userRepository.addFriend(friendEntity);
    }

    public void removeFriend(FriendshipEvent friendshipEvent) {
        FriendEntity friendEntity = userMapper.toFriendEntity(friendshipEvent);
        userRepository.removeFriend(friendEntity);
    }

}
