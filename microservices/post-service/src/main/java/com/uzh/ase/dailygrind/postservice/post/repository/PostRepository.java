package com.uzh.ase.dailygrind.postservice.post.repository;

import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.LikeEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final DynamoDbTable<PostEntity> postTable;

    private final DynamoDbTable<CommentEntity> commentTable;

    private final DynamoDbTable<LikeEntity> likeTable;

    public void savePost(PostEntity post) {
        postTable.putItem(post);
    }

    public List<PostEntity> findAllPostsForUser(String userId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(PostEntity.generatePK(userId))
                        .build());

        return postTable.query(queryConditional).items().stream().toList();
    }

    public PostEntity findPostById(String postId) {
        return postTable.scan().items().stream()
                .filter(item -> item.getPk().endsWith(PostEntity.POSTFIX))
                .filter(item -> item.getSk().equals(PostEntity.POSTFIX + "#" + postId))
                .findFirst()
                .orElse(null);
    }

    public void deletePostById(String postId, String userId) {
        String pk = PostEntity.generatePK(userId);
        String sk = PostEntity.generateSK(postId);
        postTable.deleteItem(PostEntity.builder().pk(pk).sk(sk).build());
        deleteAllCommentsForPost(postId, userId);
        deleteLikesForPost(postId);
    }

    public void likePost(LikeEntity likeEntity) {
        likeTable.putItem(likeEntity);

        PostEntity postToLike = findPostById(likeEntity.getPostId());
        postToLike.setLikeCount(postToLike.getLikeCount() + 1);
        postTable.putItem(postToLike);
    }

    public void unlikePost(LikeEntity likeEntity) {
        likeTable.deleteItem(likeEntity);

        PostEntity postToUnlike = findPostById(likeEntity.getPostId());
        postToUnlike.setLikeCount(postToUnlike.getLikeCount() - 1);
        postTable.putItem(postToUnlike);
    }

    public void deleteLikesForPost(String postId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(PostEntity.POSTFIX + "#" + postId + "LIKELIST")
                        .build());

        List<PostEntity> likes = postTable.query(queryConditional).items().stream().toList();
        for (PostEntity like : likes) {
            postTable.deleteItem(like);
        }
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

    public void saveComment(CommentEntity commentEntity) {
        commentTable.putItem(commentEntity);
        PostEntity postToComment = findPostById(commentEntity.getPostId());

        postToComment.setCommentCount(postToComment.getCommentCount() + 1);
        postTable.putItem(postToComment);
    }

    public void deleteComment(String postId, String commentId, String userId) {
        CommentEntity commentEntity = CommentEntity.builder()
                .pk(CommentEntity.generatePK(userId, postId))
                .sk(CommentEntity.generateSK(commentId))
                .build();
        commentTable.deleteItem(commentEntity);

        PostEntity postToDeleteComment = findPostById(postId);
        postToDeleteComment.setCommentCount(postToDeleteComment.getCommentCount() - 1);
        postTable.putItem(postToDeleteComment);
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

    public List<CommentEntity> findAllCommentsForPost(String userId, String postId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(CommentEntity.generatePK(userId, postId))
                        .build());

        return commentTable.query(queryConditional).items().stream().toList();
    }

    public void deleteAllPosts(String userId) {
        List<PostEntity> posts = findAllPostsForUser(userId);
        for (PostEntity post : posts) {
            deletePostById(post.getPostId(), userId);
        }
    }

    public void deleteAllComments(String userId) {
        List<CommentEntity> comments = commentTable.scan().items().stream()
                .filter(item -> item.getPk().endsWith(userId))
                .toList();
        for (CommentEntity comment : comments) {
            deleteComment(comment.getPostId(), comment.getCommentId(), userId);
        }
    }

    public void deleteAllLikes(String userId) {
        List<PostEntity> likes = postTable.scan().items().stream()
                .filter(item -> item.getPk().endsWith(userId))
                .toList();
        for (PostEntity like : likes) {
            LikeEntity likeEntity = LikeEntity.builder()
                    .pk(LikeEntity.generatePK(like.getPostId()))
                    .sk(LikeEntity.generateSK(userId))
                    .build();
            unlikePost(likeEntity);
        }
    }
}
