package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between {@link CommentDto} and {@link CommentEntity}.
 * <p>
 * This interface uses MapStruct to automatically generate the implementation for converting
 * between the data transfer object (DTO) and entity representations of a comment.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Maps a {@link CommentDto} to a {@link CommentEntity}.
     *
     * @param userId the user ID
     * @param postId the post ID
     * @param commentDto the {@link CommentDto} to map
     * @return the mapped {@link CommentEntity}
     */
    @Mapping(target = "pk", expression = "java(CommentEntity.generatePK(userId, postId))")
    @Mapping(target = "sk", expression = "java(CommentEntity.generateSK(commentDto.commentId()))")
    @Mapping(target = "commentContent", source = "commentDto.content")
    @Mapping(target = "commentTimestamp", source = "commentDto.timestamp")
    CommentEntity toCommentEntity(String userId, String postId, CommentDto commentDto);

    /**
     * Maps a {@link CommentEntity} to a {@link CommentDto}.
     *
     * @param commentEntity the {@link CommentEntity} to map
     * @return the mapped {@link CommentDto}
     */
    @Mapping(target = "commentId", expression = "java(commentEntity.getCommentId())")
    @Mapping(target = "content", source = "commentEntity.commentContent")
    @Mapping(target = "timestamp", source = "commentEntity.commentTimestamp")
    CommentDto toCommentDto(CommentEntity commentEntity);
}
