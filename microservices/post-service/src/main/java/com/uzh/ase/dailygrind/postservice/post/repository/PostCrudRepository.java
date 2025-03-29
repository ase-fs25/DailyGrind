package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.Post;
import lombok.NonNull;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

import java.util.List;

@EnableScan
public interface PostCrudRepository extends DynamoDBCrudRepository<Post, String> {
    @NonNull List<Post> findAll();
}
