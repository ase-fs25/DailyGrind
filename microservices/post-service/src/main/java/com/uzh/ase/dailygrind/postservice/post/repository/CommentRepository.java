package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final DynamoDbTable<CommentEntity> commentTable;

    public void saveComment(CommentEntity commentEntity) {
        commentTable.putItem(commentEntity);
    }

    public void deleteComment(String postId, String commentId, String userId) {
        CommentEntity commentEntity = CommentEntity.builder()
            .pk(CommentEntity.generatePK(userId, postId))
            .sk(CommentEntity.generateSK(commentId))
            .build();
        commentTable.deleteItem(commentEntity);
    }

    public List<CommentEntity> findAllCommentsForPost(String userId, String postId) {
        QueryConditional queryConditional = QueryConditional
            .keyEqualTo(Key.builder()
                .partitionValue(CommentEntity.generatePK(userId, postId))
                .build());

        return commentTable.query(queryConditional).items().stream().toList();
    }

    public void deleteAllCommentsForPost(String postId, String userId) {
        QueryConditional queryConditional = QueryConditional
            .keyEqualTo(Key.builder()
                .partitionValue(CommentEntity.generatePK(userId, postId))
                .build());

        List<CommentEntity> comments = commentTable.query(queryConditional).items().stream().toList();
        for (CommentEntity comment : comments) {
            commentTable.deleteItem(comment);
        }
    }

    public void deleteAllCommentsForUser(String userId) {
        List<CommentEntity> comments = commentTable.scan().items().stream()
            .filter(item -> item.getPk().endsWith(userId))
            .toList();
        for (CommentEntity comment : comments) {
            deleteComment(comment.getPostId(), comment.getCommentId(), userId);
        }
    }

}
