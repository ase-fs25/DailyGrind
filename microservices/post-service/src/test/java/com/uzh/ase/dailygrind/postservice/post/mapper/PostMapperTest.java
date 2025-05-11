package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.PostDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.LikeEntity;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.PostEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PostMapperTest {

    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Test
    void toPostEntity_shouldMapCorrectlyWithCounts() {
        String userId = "user123";
        PostDto dto = new PostDto("post789", "Title", "Content",
            Instant.parse("2025-05-11T10:00:00Z").toString(), 5L, 2L, false, false);

        PostEntity entity = postMapper.toPostEntity(userId, dto);

        assertThat(entity.getPk()).isEqualTo("USER#user123#POST");
        assertThat(entity.getSk()).isEqualTo("POST#post789");
        assertThat(entity.getPostTitle()).isEqualTo("Title");
        assertThat(entity.getPostContent()).isEqualTo("Content");
        assertThat(entity.getPostTimestamp()).isEqualTo(Instant.parse("2025-05-11T10:00:00Z").toString());
        assertThat(entity.getLikeCount()).isEqualTo(5);
        assertThat(entity.getCommentCount()).isEqualTo(2);
    }

    @Test
    void toPostEntity_shouldDefaultCountsToZero() {
        String userId = "user123";
        PostDto dto = new PostDto("post789", "Title", "Content",
            Instant.parse("2025-05-11T10:00:00Z").toString(), null, null, false, false);

        PostEntity entity = postMapper.toPostEntity(userId, dto);

        assertThat(entity.getLikeCount()).isZero();
        assertThat(entity.getCommentCount()).isZero();
    }

    @Test
    void toLikeEntity_shouldMapCorrectly() {
        String postId = "post123";
        String userId = "user456";

        LikeEntity like = postMapper.toLikeEntity(postId, userId);

        assertThat(like.getPk()).isEqualTo("POST#post123#LIKE");
        assertThat(like.getSk()).isEqualTo("USER#user456");
    }

    @Test
    void toPostDto_shouldMapCorrectly() {
        PostEntity entity = new PostEntity();
        entity.setPk("USER#user123");
        entity.setSk("POST#post789");
        entity.setPostTitle("Mapped Title");
        entity.setPostContent("Mapped Content");
        entity.setPostTimestamp(Instant.parse("2025-05-11T11:00:00Z").toString());
        entity.setLikeCount(3L);
        entity.setCommentCount(1L);

        PostDto dto = postMapper.toPostDto(entity, true, true);

        assertThat(dto.postId()).isEqualTo("post789");
        assertThat(dto.title()).isEqualTo("Mapped Title");
        assertThat(dto.content()).isEqualTo("Mapped Content");
        assertThat(dto.timestamp()).isEqualTo(Instant.parse("2025-05-11T11:00:00Z").toString());
        assertThat(dto.likeCount()).isEqualTo(3);
        assertThat(dto.commentCount()).isEqualTo(1);
        assertThat(dto.isLiked()).isTrue();
        assertThat(dto.isPinned()).isTrue();
    }
}
