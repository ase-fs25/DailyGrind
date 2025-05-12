package com.uzh.ase.dailygrind.postservice.post.mapper;

import com.uzh.ase.dailygrind.postservice.post.controller.dto.CommentDto;
import com.uzh.ase.dailygrind.postservice.post.repository.entity.CommentEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toCommentEntity_shouldMapCorrectly() {
        String userId = "user123";
        String postId = "post456";
        CommentDto dto = new CommentDto(null, userId, "Nice post!", Instant.parse("2025-05-11T12:00:00Z").toString());

        CommentEntity entity = commentMapper.toCommentEntity(userId, postId, dto);

        assertThat(entity.getPk()).isEqualTo("USER#user123#POST#post456#COMMENT");
        assertThat(entity.getSk()).startsWith("COMMENT#");
        assertThat(entity.getCommentContent()).isEqualTo("Nice post!");
        assertThat(entity.getCommentTimestamp()).isEqualTo(Instant.parse("2025-05-11T12:00:00Z").toString());
    }

    @Test
    void toCommentEntity_shouldMapCorrectlyWithCommentId() {
        String userId = "user123";
        String postId = "post456";
        String commentId = "comment789";
        CommentDto dto = new CommentDto(commentId, userId, "Nice post!", Instant.parse("2025-05-11T12:00:00Z").toString());

        CommentEntity entity = commentMapper.toCommentEntity(userId, postId, dto);

        assertThat(entity.getPk()).isEqualTo("USER#user123#POST#post456#COMMENT");
        assertThat(entity.getSk()).isEqualTo("COMMENT#comment789");
        assertThat(entity.getCommentContent()).isEqualTo("Nice post!");
        assertThat(entity.getCommentTimestamp()).isEqualTo(Instant.parse("2025-05-11T12:00:00Z").toString());
    }

    @Test
    void toCommentDto_shouldMapCorrectly() {
        CommentEntity entity = new CommentEntity();
        entity.setPk("USER#user123#POST#post456");
        entity.setSk("COMMENT#comment789");
        entity.setCommentContent("Thanks for sharing.");
        entity.setCommentTimestamp(Instant.parse("2025-05-11T13:00:00Z").toString());

        CommentDto dto = commentMapper.toCommentDto(entity);

        assertThat(dto.commentId()).isEqualTo("comment789");
        assertThat(dto.content()).isEqualTo("Thanks for sharing.");
        assertThat(dto.timestamp()).isEqualTo(Instant.parse("2025-05-11T13:00:00Z").toString());
    }

}
