package com.communication.dto;

import com.communication.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDto {

    private Long id;
    private Long contentId;
    private UserDto user;
    private String body;
    private Long parentId;
    private List<CommentDto> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentDto() {}

    public CommentDto(Long id, Long contentId, UserDto user, String body, Long parentId,
                      List<CommentDto> replies, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contentId = contentId;
        this.user = user;
        this.body = body;
        this.parentId = parentId;
        this.replies = replies;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public List<CommentDto> getReplies() { return replies; }
    public void setReplies(List<CommentDto> replies) { this.replies = replies; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static CommentDto fromEntity(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .contentId(comment.getContent().getId())
                .user(UserDto.fromEntity(comment.getUser()))
                .body(comment.getBody())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public static CommentDto fromEntityWithReplies(Comment comment) {
        CommentDto dto = fromEntity(comment);
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream()
                    .map(CommentDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static CommentDtoBuilder builder() { return new CommentDtoBuilder(); }

    public static class CommentDtoBuilder {
        private Long id;
        private Long contentId;
        private UserDto user;
        private String body;
        private Long parentId;
        private List<CommentDto> replies;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public CommentDtoBuilder id(Long id) { this.id = id; return this; }
        public CommentDtoBuilder contentId(Long contentId) { this.contentId = contentId; return this; }
        public CommentDtoBuilder user(UserDto user) { this.user = user; return this; }
        public CommentDtoBuilder body(String body) { this.body = body; return this; }
        public CommentDtoBuilder parentId(Long parentId) { this.parentId = parentId; return this; }
        public CommentDtoBuilder replies(List<CommentDto> replies) { this.replies = replies; return this; }
        public CommentDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public CommentDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public CommentDto build() {
            return new CommentDto(id, contentId, user, body, parentId, replies, createdAt, updatedAt);
        }
    }
}
