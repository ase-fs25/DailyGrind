package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.Post;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBPagingAndSortingRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;

@EnableScan
@EnableScanCount
public interface PostPagingSortingRepository extends DynamoDBPagingAndSortingRepository<Post, String> {
    Post findByPostId(String postId);
}
