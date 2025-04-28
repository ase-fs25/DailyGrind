package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.DailyPostEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "pk", expression = "java(PostEntity.generatePK(userId))")
    @Mapping(target = "sk", expression = "java(PostEntity.generateSK(postDto.postId()))")
    @Mapping(target = "postTitle", source = "postDto.title")
    @Mapping(target = "postContent", source = "postDto.content")
    @Mapping(target = "postTimestamp", source = "postDto.timestamp")
    @Mapping(target = "likeCount", expression = "java(postDto.likeCount() == null ? 0 : postDto.likeCount())")
    @Mapping(target = "commentCount", expression = "java(postDto.commentCount() == null ? 0 : postDto.commentCount())")
    PostEntity toPostEntity(String userId, PostDto postDto);

    @Mapping(target = "pk", expression = "java(CommentEntity.generatePK(userId, postId))")
    @Mapping(target = "sk", expression = "java(CommentEntity.generateSK(commentDto.commentId()))")
    @Mapping(target = "commentContent", source = "commentDto.content")
    @Mapping(target = "commentTimestamp", source = "commentDto.timestamp")
    CommentEntity toCommentEntity(String userId, String postId, CommentDto commentDto);

    @Mapping(target = "postId", expression = "java(postEntity.getPostId())")
    @Mapping(target = "title", source = "postEntity.postTitle")
    @Mapping(target = "content", source = "postEntity.postContent")
    @Mapping(target = "timestamp", source = "postEntity.postTimestamp")
    PostDto toPostDto(PostEntity postEntity);

    @Mapping(target = "commentId", expression = "java(commentEntity.getCommentId())")
    @Mapping(target = "content", source = "commentEntity.commentContent")
    @Mapping(target = "timestamp", source = "commentEntity.commentTimestamp")
    CommentDto toCommentDto(CommentEntity commentEntity);

}
