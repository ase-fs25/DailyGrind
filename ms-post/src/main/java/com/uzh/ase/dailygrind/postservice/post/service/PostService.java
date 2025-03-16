package com.uzh.ase.dailygrind.postservice.post.service;

import com.uzh.ase.dailygrind.postservice.post.repository.PostCrudRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.PostPagingSortingRepository;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCrudRepository postCrudRepository;

    private final PostPagingSortingRepository postPagingSortingRepository;

    public List<Post> getAllPosts() {
        return postCrudRepository.findAll();
    }

    public Post createPost(Post post, String userId) {
        post.setUserId(userId);
        post.setTimestamp(LocalDateTime.now().toString());
        return postCrudRepository.save(post);
    }

    public Post getPost(String postId) {
        return postPagingSortingRepository.findByPostId(postId);
    }
}
