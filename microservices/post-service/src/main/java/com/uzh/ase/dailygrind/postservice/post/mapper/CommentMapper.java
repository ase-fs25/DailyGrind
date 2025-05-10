package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Maps a CommentDto to a CommentEntity.
     *
     * @param userId the user ID
     * @param postId the post ID
     * @param commentDto the CommentDto to map
     * @return the mapped CommentEntity
     */
    @Mapping(target = "pk", expression = "java(CommentEntity.generatePK(userId, postId))")
    @Mapping(target = "sk", expression = "java(CommentEntity.generateSK(commentDto.commentId()))")
    @Mapping(target = "commentContent", source = "commentDto.content")
    @Mapping(target = "commentTimestamp", source = "commentDto.timestamp")
    CommentEntity toCommentEntity(String userId, String postId, CommentDto commentDto);

    /**
     * Maps a CommentEntity to a CommentDto.
     *
     * @param commentEntity the CommentEntity to map
     * @return the mapped CommentDto
     */
    @Mapping(target = "commentId", expression = "java(commentEntity.getCommentId())")
    @Mapping(target = "content", source = "commentEntity.commentContent")
    @Mapping(target = "timestamp", source = "commentEntity.commentTimestamp")
    CommentDto toCommentDto(CommentEntity commentEntity);

}
