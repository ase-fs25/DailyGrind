package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Named("generateId")
    default String generateId(String prefix, String postfix, String id, String secondaryId) {
        if (id == null) id = UUID.randomUUID().toString();
        if (secondaryId == null) secondaryId = "";
        if (prefix == null || id.startsWith(prefix)) prefix = "";
        else prefix = prefix + "#";
        if (postfix == null || id.endsWith(postfix)) postfix = "";
        else postfix = "#" + postfix;
        return prefix + id + postfix + (secondaryId.isEmpty() ? "" : "#" + secondaryId);
    }

    @Mapping(target = "pk", expression = "java(generateId(PostEntity.PREFIX, userId, PostEntity.POSTFIX, null))")
    @Mapping(target = "sk", expression = "java(generateId(PostEntity.POSTFIX, null, postDto.postId(), null))")
    @Mapping(target = "postTitle", source = "postDto.title")
    @Mapping(target = "postContent", source = "postDto.content")
    @Mapping(target = "postTimestamp", expression = "java(java.time.Instant.now().toString())")
    PostEntity toPostEntity(String userId, PostDto postDto);

    @Mapping(target = "pk", expression = "java(generateId(PostEntity.POSTFIX, CommentEntity.ID_NAME, postId, null))")
    @Mapping(target = "sk", expression = "java(generateId(CommentEntity.ID_NAME, PostEntity.POSTFIX, commentDto.commentId(), userId))")
    @Mapping(target = "commentContent", source = "commentDto.content")
    @Mapping(target = "commentTimestamp", expression = "java(java.time.Instant.now().toString())")
    CommentEntity toCommentEntity(String userId, String postId, CommentDto commentDto);

    @Mapping(target = "postId", expression = "java(postEntity.getSk().split(\"#\")[1])")
    @Mapping(target = "title", source = "postEntity.postTitle")
    @Mapping(target = "content", source = "postEntity.postContent")
    @Mapping(target = "timestamp", source = "postEntity.postTimestamp")
    PostDto toPostDto(PostEntity postEntity);
}
