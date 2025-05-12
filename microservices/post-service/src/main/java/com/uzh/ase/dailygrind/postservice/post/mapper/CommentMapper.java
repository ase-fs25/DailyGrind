package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link CommentDto} and {@link CommentEntity}.
 * <p>
 * This interface provides methods to map data between the Comment Data Transfer Object (DTO)
 * and the Comment Entity used in the repository.
 * The mapping ensures that necessary fields are correctly transferred between these two objects.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Maps a {@link CommentDto} to a {@link CommentEntity}.
     * <p>
     * This method maps the {@link CommentDto} to a {@link CommentEntity}, ensuring that the relevant
     * fields such as the primary key (PK) and sort key (SK) are generated, and the content and timestamp
     * from the DTO are mapped to the entity.
     *
     * @param userId the user ID associated with the comment
     * @param postId the post ID associated with the comment
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
     * <p>
     * This method maps the {@link CommentEntity} to a {@link CommentDto}, transferring the entity's
     * comment ID, content, and timestamp to the corresponding fields in the DTO.
     *
     * @param commentEntity the {@link CommentEntity} to map
     * @return the mapped {@link CommentDto}
     */
    @Mapping(target = "commentId", expression = "java(commentEntity.getCommentId())")
    @Mapping(target = "content", source = "commentEntity.commentContent")
    @Mapping(target = "timestamp", source = "commentEntity.commentTimestamp")
    CommentDto toCommentDto(CommentEntity commentEntity);

}
