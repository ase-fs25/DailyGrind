package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.LikeEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    /**
     * Maps a PostDto to a PostEntity.
     *
     * @param userId the user ID
     * @param postDto the PostDto to map
     * @return the mapped PostEntity
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
     * Maps a LikeEntity to a PostDto.
     *
     * @param postId the post ID
     * @param userId the user ID
     * @return the mapped LikeEntity
     */
    @Mapping(target = "pk", expression = "java(LikeEntity.generatePK(postId))")
    @Mapping(target = "sk", expression = "java(LikeEntity.generateSK(userId))")
    LikeEntity toLikeEntity(String postId, String userId);

    /**
     * Maps a PostEntity to a PostDto.
     *
     * @param postEntity the PostEntity to map
     * @return the mapped PostDto
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
