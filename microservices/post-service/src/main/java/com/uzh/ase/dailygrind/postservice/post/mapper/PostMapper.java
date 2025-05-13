package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.LikeEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between {@link PostDto} and {@link PostEntity}.
 * <p>
 * This interface uses MapStruct to automatically generate the implementation for converting
 * between the data transfer object (DTO) and entity representations of a post and its likes.
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    /**
     * Maps a {@link PostDto} to a {@link PostEntity}.
     *
     * @param userId the user ID
     * @param postDto the {@link PostDto} to map
     * @return the mapped {@link PostEntity}
     */
    @Mapping(target = "pk", expression = "java(PostEntity.generatePK(userId))")
    @Mapping(target = "sk", expression = "java(PostEntity.generateSK(postDto.postId()))")
    @Mapping(target = "postTitle", source = "postDto.title")
    @Mapping(target = "postContent", source = "postDto.content")
    @Mapping(target = "postTimestamp", source = "postDto.timestamp")
    @Mapping(target = "likeCount", expression = "java(postDto.likeCount() == null ? 0 : postDto.likeCount())")
    @Mapping(target = "commentCount", expression = "java(postDto.commentCount() == null ? 0 : postDto.commentCount())")
    PostEntity toPostEntity(String userId, PostDto postDto);

    /**
     * Maps a {@link LikeEntity} to a {@link PostDto}.
     * <p>
     * This method creates a {@link LikeEntity} based on the post ID and user ID.
     *
     * @param postId the post ID
     * @param userId the user ID
     * @return the mapped {@link LikeEntity}
     */
    @Mapping(target = "pk", expression = "java(LikeEntity.generatePK(postId))")
    @Mapping(target = "sk", expression = "java(LikeEntity.generateSK(userId))")
    LikeEntity toLikeEntity(String postId, String userId);

    /**
     * Maps a {@link PostEntity} to a {@link PostDto}.
     *
     * @param postEntity the {@link PostEntity} to map
     * @param isLiked whether the post is liked by the authenticated user
     * @param isPinned whether the post is pinned
     * @return the mapped {@link PostDto}
     */
    @Mapping(target = "postId", expression = "java(postEntity.getPostId())")
    @Mapping(target = "title", source = "postEntity.postTitle")
    @Mapping(target = "content", source = "postEntity.postContent")
    @Mapping(target = "timestamp", source = "postEntity.postTimestamp")
    @Mapping(target = "likeCount", source = "postEntity.likeCount")
    @Mapping(target = "commentCount", source = "postEntity.commentCount")
    @Mapping(target = "isLiked", source = "isLiked")
    @Mapping(target = "isPinned", source = "isPinned")
    PostDto toPostDto(PostEntity postEntity, boolean isLiked, boolean isPinned);
}
