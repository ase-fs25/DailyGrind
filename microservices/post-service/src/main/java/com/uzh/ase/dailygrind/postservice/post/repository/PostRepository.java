package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final DynamoDbTable<PostEntity> postTable;

    private final DynamoDbTable<CommentEntity> commentTable;

    public void savePost(PostEntity post) {
        postTable.putItem(post);
    }

    public List<PostEntity> findAllPostsForUser(String userId) {
        return postTable.scan().items().stream()
                .filter(item -> item.getPk().equals(PostEntity.PREFIX + "#" + userId + "#" + PostEntity.POSTFIX))
                .toList();
    }

    public PostEntity findPostById(String postId) {
        return postTable.scan().items().stream()
                .filter(item -> item.getSk().equals(PostEntity.POSTFIX + "#" + postId))
                .findFirst()
                .orElse(null);
    }

    public void likePost(String postId, String userId) {
        PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.POSTFIX + "#" + postId + "LIKELIST")
                .sk(PostEntity.PREFIX + "#" + userId)
                .build();
        postTable.putItem(postEntity);
    }

    public void unlikePost(String postId, String userId) {
        PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.POSTFIX + "#" + postId + "LIKELIST")
                .sk(PostEntity.PREFIX + "#" + userId)
                .build();
        postTable.deleteItem(postEntity);
    }

    public void saveComment(CommentEntity commentEntity) {
        commentTable.putItem(commentEntity);
    }

    public void deleteEntity(String pk, String sk) {
        PostEntity postEntity = PostEntity.builder()
                .pk(pk)
                .sk(sk)
                .build();
        postTable.deleteItem(postEntity);
    }

    public List<String> getTimelineForUser(String userId) {
        return postTable.scan().items().stream()
                .filter(item -> item.getPk().equals(PostEntity.PREFIX + "#" + userId + "#" + "TIMELINE"))
                .map(PostEntity::getSk)
                .toList();
    }

    public void saveTimelineEntity(String friendId, String sk) {
        PostEntity postEntity = PostEntity.builder()
                .pk(PostEntity.PREFIX + "#" + friendId + "#" + "TIMELINE")
                .sk(sk)
                .build();
        postTable.putItem(postEntity);
    }
}
